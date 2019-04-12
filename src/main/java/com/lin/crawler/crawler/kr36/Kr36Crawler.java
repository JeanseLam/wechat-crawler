package com.lin.crawler.crawler.kr36;

import com.lin.crawler.context.AbstractCrawler;
import com.lin.crawler.context.Crawler;
import com.lin.crawler.context.LocalCrawlerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * Created by linjinzhi on 2018-12-17.
 *
 * 36氪抓取.
 *
 */
@Component
@Crawler(source = "36kr")
public class Kr36Crawler extends AbstractCrawler {

    private static final Logger logger = LoggerFactory.getLogger(Kr36Crawler.class);

    @Override
    public String crawlHomeRequest(LocalCrawlerSession crawlerSession) {

        return crawlerSession.getCrawlerConfig().homePageRequest;
    }

    @Override
    public String crawlNextPageRequest(LocalCrawlerSession crawlerSession) {
        // 36氪暂时只抓取首页新闻
        return null;
    }

    @Override
    public String auth(LocalCrawlerSession crawlerSession, Object pageContent, String request) {
        // 36氪抓取过程不需要鉴权
        return (String) pageContent;
    }
}
