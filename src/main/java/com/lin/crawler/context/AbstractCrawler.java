package com.lin.crawler.context;

import com.lin.crawler.business.NewsArticleService;
import com.lin.crawler.common.ApplicationContextUtils;
import com.lin.crawler.common.entity.NewsArticle;
import com.lin.crawler.common.httpclient.RequestUtils;
import com.lin.crawler.common.httpclient.HttpRequestData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *
 * Created by linjinzhi on 2018-12-12.
 *
 * 基础爬虫抽象类.
 *
 */
public abstract class AbstractCrawler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCrawler.class);

    private NewsArticleService newsArticleService = ApplicationContextUtils.getBean(NewsArticleService.class);

    private AbstractProcessor processor;

    public abstract String crawlHomeRequest(LocalCrawlerSession crawlerSession);

    public abstract String crawlNextPageRequest(LocalCrawlerSession crawlerSession);

    public abstract String auth(LocalCrawlerSession crawlerSession, Object pageContent, String request);


    /**
     * 抓取文章列表数据
     * @param crawlerSession 爬虫会话数据封装
     */
    public void crawlArticleList(LocalCrawlerSession crawlerSession) {

        HttpRequestData httpRequestData = crawlerSession.getHttpRequestData();

        // 抓取和解析首页
        String homeRequest = crawlHomeRequest(crawlerSession);
        crawlerSession.getArticleListUrls().offer(homeRequest);

        // 抓取解析列表页
        while (!crawlerSession.getArticleListUrls().isEmpty()) {

            try {
                String request = crawlerSession.getArticleListUrls().poll();
                if(StringUtils.isNotBlank(request) && (request.startsWith("http") || request.startsWith("https"))) {
                    String html = RequestUtils.get(httpRequestData, request);

                    // 判断是否需要验证，如果需要验证则处理
                    html = auth(crawlerSession, html, request);

                    List<NewsArticle> newsArticles = this.processor.processListPage(html);

                    for(NewsArticle article : newsArticles) {
                        if(!newsArticleService.isExist(article.getTitle())) {
                            crawlerSession.getArticles().add(article);
                        }
                    }

                    String nextPageUrl = crawlNextPageRequest(crawlerSession);
                    if(StringUtils.isNotBlank(nextPageUrl)) {
                        crawlerSession.getArticleListUrls().offer(nextPageUrl);
                    }
                }

                Thread.sleep(100L);

            } catch (Exception e) {
                logger.error("crawl article list page occurs an error, message:{}, exception:{}", e.getMessage(), e);
            }
        }

        logger.info("crawl and process article list page finish");
    }


    /**
     * 抓取文章详情数据
     * @param crawlerSession 爬虫会话数据封装
     */
    public void crawlArticleDetail(LocalCrawlerSession crawlerSession) {

        // 抓取解析文章详情页面
        HttpRequestData httpRequestData = crawlerSession.getHttpRequestData();
        List<NewsArticle> newsArticles = crawlerSession.getArticles();
        for(NewsArticle newsArticle : newsArticles) {
            try {
                String detailRequest = newsArticle.getDetailLink();

                String html = "";
                if(!crawlerSession.getRequestHeaders().isEmpty()) {
                    html = RequestUtils.get(httpRequestData, detailRequest, null, crawlerSession.getRequestHeaders());
                } else {
                    html = RequestUtils.get(httpRequestData, detailRequest);
                }

                newsArticle.setSource(crawlerSession.getCrawlerConfig().source);
                newsArticle.setSourceType(crawlerSession.getCrawlerConfig().sourceType);
                newsArticle.setTopic(crawlerSession.getCrawlerConfig().topic);

                processor.processDetailPage(html, newsArticle);

            } catch (Exception e) {
                logger.error("crawl article detail page occurs an error, message:{}, exception:{}", e.getMessage(), e);
            }
        }
        logger.info("crawl and process article detail pages finish");

        // 处理图片和html格式
        processor.processImageLink(crawlerSession);
        processor.processHtmlContentFormat(crawlerSession.getArticles());
        logger.info("process image link and html content format finish");
    }

    /**
     * 绑定结果集处理器
     * @param processor 结果集处理器
     */
    public void bindResultProcess(AbstractProcessor processor) {
        this.processor = processor;
    }

    public AbstractProcessor getProcessor() {
        return processor;
    }
}
