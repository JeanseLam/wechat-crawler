package com.lin.crawler;

import com.lin.crawler.common.httpclient.HttpRequestData;
import com.lin.crawler.context.AbstractCrawler;
import com.lin.crawler.context.AbstractProcessor;
import com.lin.crawler.context.CrawlerConfig;
import com.lin.crawler.context.LocalCrawlerSession;
import com.lin.crawler.crawler.kr36.Kr36Crawler;
import com.lin.crawler.crawler.kr36.Kr36Processor;
import com.lin.crawler.crawler.wallstreet.WallStreetCrawler;
import com.lin.crawler.crawler.wallstreet.WallStreetProcessor;
import com.lin.crawler.crawler.wechat.WechatCrawler;
import com.lin.crawler.crawler.wechat.WechatProcessor;
import com.lin.crawler.crawler.xuangubao.XuangubaoCrawler;
import com.lin.crawler.crawler.xuangubao.XuangubaoProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AllCrawlerTest {

    @Resource
    private ApplicationContext applicationContext;

    @Test
    public void testWechatCrawler() {
        CrawlerConfig crawlerConfig = new CrawlerConfig();
        crawlerConfig.source = "wechat";
        crawlerConfig.topic = "正解局";
        crawlerConfig.sourceType = 0;
        crawlerConfig.processClass = "com.lin.crawler.crawler.wechat.WechatProcessor";

        HttpRequestData httpRequestData = new HttpRequestData();
        LocalCrawlerSession crawlerSession = new LocalCrawlerSession();
        crawlerSession.setCrawlerConfig(crawlerConfig);
        crawlerSession.setCookieStore(httpRequestData.getCookieStore());
        crawlerSession.setHttpRequestData(httpRequestData);

        AbstractProcessor processor = new WechatProcessor();
        AbstractCrawler wechatCrawler = applicationContext.getBean(WechatCrawler.class);
        wechatCrawler.bindResultProcess(processor);

        wechatCrawler.crawlArticleList(crawlerSession);
        wechatCrawler.crawlArticleDetail(crawlerSession);
    }


    @Test
    public void test36KrCrawler() {
        CrawlerConfig crawlerConfig = new CrawlerConfig();
        crawlerConfig.source = "36kr";
        crawlerConfig.topic = "";
        crawlerConfig.sourceType = 1;
        crawlerConfig.processClass = "com.caimi.spider.crawler.kr36.Kr36Processor";
        crawlerConfig.homePageRequest = "https://36kr.com/";

        HttpRequestData httpRequestData = new HttpRequestData();
        LocalCrawlerSession crawlerSession = new LocalCrawlerSession();
        crawlerSession.setCrawlerConfig(crawlerConfig);
        crawlerSession.setCookieStore(httpRequestData.getCookieStore());
        crawlerSession.setHttpRequestData(httpRequestData);

        AbstractProcessor processor = new Kr36Processor();
        AbstractCrawler kr36Crawler = applicationContext.getBean(Kr36Crawler.class);
        kr36Crawler.bindResultProcess(processor);

        kr36Crawler.crawlArticleList(crawlerSession);
        kr36Crawler.crawlArticleDetail(crawlerSession);
    }


    @Test
    public void testXuangubaoCrawler() {
        CrawlerConfig crawlerConfig = new CrawlerConfig();
        crawlerConfig.source = "xuangubao";
        crawlerConfig.topic = "";
        crawlerConfig.sourceType = 1;
        crawlerConfig.processClass = "com.lin.crawler.crawler.xuangubao.XuangubaoProcessor";
        crawlerConfig.homePageRequest = "https://xuangubao.cn/yuanchuang";

        HttpRequestData httpRequestData = new HttpRequestData();
        LocalCrawlerSession crawlerSession = new LocalCrawlerSession();
        crawlerSession.setCrawlerConfig(crawlerConfig);
        crawlerSession.setCookieStore(httpRequestData.getCookieStore());
        crawlerSession.setHttpRequestData(httpRequestData);

        AbstractProcessor processor = new XuangubaoProcessor();
        AbstractCrawler xuangubaoCrawler = applicationContext.getBean(XuangubaoCrawler.class);
        xuangubaoCrawler.bindResultProcess(processor);

        xuangubaoCrawler.crawlArticleList(crawlerSession);
        xuangubaoCrawler.crawlArticleDetail(crawlerSession);
    }


    @Test
    public void testWallStreetCrawler() {
        CrawlerConfig crawlerConfig = new CrawlerConfig();
        crawlerConfig.source = "wallStreet";
        crawlerConfig.topic = "";
        crawlerConfig.sourceType = 1;
        crawlerConfig.processClass = "com.lin.crawler.crawler.wallstreet.WallStreetProcessor";
        crawlerConfig.homePageRequest = "https://wallstreetcn.com/news/global";

        HttpRequestData httpRequestData = new HttpRequestData();
        LocalCrawlerSession crawlerSession = new LocalCrawlerSession();
        crawlerSession.setCrawlerConfig(crawlerConfig);
        crawlerSession.setCookieStore(httpRequestData.getCookieStore());
        crawlerSession.setHttpRequestData(httpRequestData);

        AbstractProcessor processor = new WallStreetProcessor();
        AbstractCrawler crawler = applicationContext.getBean(WallStreetCrawler.class);
        crawler.bindResultProcess(processor);

        crawler.crawlArticleList(crawlerSession);
        crawler.crawlArticleDetail(crawlerSession);
    }
}
