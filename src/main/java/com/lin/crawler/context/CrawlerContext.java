package com.lin.crawler.context;

import com.lin.crawler.business.NewsArticleService;
import com.lin.crawler.common.ScyllaProxyCenter;
import com.lin.crawler.common.SpringContextUtils;
import com.lin.crawler.common.entity.SimpleProxyIp;
import com.lin.crawler.common.httpclient.HttpRequestData;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * Created by linjinzhi on 2018-12-12.
 *
 * 爬虫任务容器，启动主线程.
 *
 */
@Component
@ConfigurationProperties(prefix = "thread")
public class CrawlerContext {

    private static final Logger logger = LoggerFactory.getLogger(CrawlerContext.class);

    private AtomicInteger crawlArticleCount = new AtomicInteger(0);

    @Resource
    private NewsArticleService newsArticleService;

    @Resource
    private ScyllaProxyCenter scyllaProxyCenter;

    private boolean multiThreadMode;

    /**
     * 线程池核心数
     */
    private int corePoolSize;

    /**
     * 线程池最大数
     */
    private int maximumPoolSize;

    /**
     * 任务队列尺寸
     */
    private int queueSize;

    private MyThreadPoolExecutor threadPoolExecutor;

    private List<AbstractCrawler> crawlers;

    private Set<CrawlerConfig> crawlerConfigs = new HashSet<>();

    /**
     * 容器初始化
     */
    public void init() {
        // 初始化线程池
        this.threadPoolExecutor =
                new MyThreadPoolExecutor(
                        corePoolSize, maximumPoolSize, 0L, TimeUnit.MILLISECONDS,
                        new ArrayBlockingQueue<Runnable>(queueSize),
                        new ThreadFactoryBuilder().setNameFormat("work thread-%d").build(),
                        new ThreadPoolExecutor.CallerRunsPolicy());

        // 读取爬虫
        this.crawlers = new ArrayList<>();
        List<Object> crawlerBeans = SpringContextUtils.getBeansWithAnnotation(Crawler.class);
        if(crawlerBeans == null || crawlerBeans.isEmpty()) {
            logger.warn("there are no crawlers in context");
            return;
        }
        for(Object bean : crawlerBeans) {
            if(bean instanceof AbstractCrawler) {
                AbstractCrawler crawler = (AbstractCrawler) bean;
                this.crawlers.add(crawler);
            }
        }

        // 读取爬虫配置
        CrawlerConfigParser configParser = new CrawlerConfigParser();
        Set<CrawlerConfig> configs = configParser.parse();
        if(configs == null || configs.isEmpty()) {
            logger.warn("crawler config is empty");
            return;
        }
        this.crawlerConfigs.addAll(configs);
    }


    /**
     * 启动主线程
     */
    public void run() {

        while (true) {

            CountDownLatch workerLatch = new CountDownLatch(this.crawlerConfigs.size());

            // 爬虫配置列表转换成队列，主要是为了在使用代理模式下等待可用代理
            Queue<CrawlerConfig> crawlerConfigQueue = new LinkedList<>();
            crawlerConfigQueue.addAll(this.crawlerConfigs);

            while (!crawlerConfigQueue.isEmpty()) {

                CrawlerConfig crawlerConfig = crawlerConfigQueue.poll();
                if(crawlerConfig != null) {
                    AbstractCrawler crawler = getCrawlerBySourceAndTopic(crawlerConfig.source, crawlerConfig.topic);
                    if(crawler == null) {
                        logger.warn("there is no crawler source:{}, topic:{}", crawlerConfig.source, crawlerConfig.topic);
                        continue;
                    }

                    // 构建本地爬虫会话
                    HttpRequestData httpRequestData;
                    if(scyllaProxyCenter.getOn().equals("true")) {
                        SimpleProxyIp simpleProxyIp = scyllaProxyCenter.applyProxyIp();

                        // 没有可用代理，任务配置重新加入队列
                        if(simpleProxyIp == null) {
                            crawlerConfigQueue.offer(crawlerConfig);
                            continue;
                        }
                        httpRequestData = new HttpRequestData(simpleProxyIp.getIp(), simpleProxyIp.getPort(), null, false);
//                        httpRequestData = new HttpRequestData("10.250.100.42", 8081, null, false);

                    } else {
                        httpRequestData = new HttpRequestData();
                    }
                    LocalCrawlerSession crawlerSession = new LocalCrawlerSession();
                    crawlerSession.setHttpRequestData(httpRequestData);
                    crawlerSession.setArticles(new LinkedList<>());
                    crawlerSession.setCookieStore(httpRequestData.getCookieStore());
                    crawlerSession.setCrawlerConfig(crawlerConfig);

                    // 实例化结果处理器
                    AbstractProcessor processor = null;
                    try {
                        Class processorClass = Class.forName(crawlerConfig.processClass);
                        processor = (AbstractProcessor) processorClass.newInstance();
                    } catch (Exception e) {
                        logger.error("create processor instance by class occurs an error, message:{}, exception:{}", e.getMessage(), e);
                        continue;
                    }

                    CrawlTask crawlTask = new CrawlTask();
                    crawlTask.init(crawler, crawlerSession, processor, workerLatch);

                    // 判断运行机制
                    if(multiThreadMode) {
                        threadPoolExecutor.submit(crawlTask);
                    } else {
                        crawlTask.run();
                    }
                }
            }

            // 全部任务执行完成，开始倒计时
            try {
                logger.info("waiting for all crawl task finish...");
                workerLatch.await();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            logger.info("crawl article count:{} on this crawler run period", crawlArticleCount.get());

            // 倒计时
            logger.info("waiting next run period...");
            CountDownLatch countDownLatch = new CountDownLatch(1);
            final Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {

                // 主线程运行时间间隔, 设置4小时
                int taskTimeIdle = 3600 * 6;
                public void run() {
                    taskTimeIdle--;
                    if (taskTimeIdle < 0) {
                        timer.cancel();
                        countDownLatch.countDown();
                    }
                }
            }, 0, 1000);

            try {
                countDownLatch.await();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public boolean isMultiThreadMode() {
        return multiThreadMode;
    }

    public void setMultiThreadMode(boolean multiThreadMode) {
        this.multiThreadMode = multiThreadMode;
    }

    private AbstractCrawler getCrawlerBySourceAndTopic(String source, String topic) {
        for(AbstractCrawler crawler : this.crawlers) {
            Crawler crawlerAnnotation = crawler.getClass().getAnnotation(Crawler.class);
            if(source.equals(crawlerAnnotation.source())) {
                return crawler;
            }
        }
        return null;
    }


    /**
     * 爬虫抓取解析任务.
     */
    private class CrawlTask implements Runnable {

        private AbstractCrawler crawler;
        private AbstractProcessor processor;
        private LocalCrawlerSession crawlerSession;

        /**
         * 用于阻塞主线程，等待全部抓取解析任务完成
         */
        private CountDownLatch countDownLatch;

        public void init(AbstractCrawler crawler, LocalCrawlerSession crawlerSession,
                         AbstractProcessor processor, CountDownLatch countDownLatch) {
            this.crawler = crawler;
            this.crawlerSession = crawlerSession;
            this.processor = processor;
            this.crawler.bindResultProcess(this.processor);

            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {

            MDC.clear();
            try {

                MDC.put("source", crawlerSession.getCrawlerConfig().source);
                MDC.put("topic", crawlerSession.getCrawlerConfig().topic);
                MDC.put("source_type", crawlerSession.getCrawlerConfig().sourceType + "");

                logger.info("crawl task source:{}, topic:{} start...", crawlerSession.getCrawlerConfig().source, crawlerSession.getCrawlerConfig().topic);

                crawler.crawlArticleList(crawlerSession);
                crawler.crawlArticleDetail(crawlerSession);

                Set<Long> idSet = newsArticleService.add(crawlerSession.getArticles());
                logger.info("save ids size:{}", idSet.size());
                crawlArticleCount.addAndGet(idSet.size());

                // 打标签并转换成用户文章
                newsArticleService.convertCrawlArticleToUserArticle(crawlerSession.getArticles());

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                MDC.clear();
                countDownLatch.countDown();
            }
        }
    }
}
