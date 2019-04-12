package com.lin.crawler.crawler.xuangubao;

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
 * 选股宝
 *
 */
@Component
@Crawler(source = "xuangubao")
public class XuangubaoCrawler extends AbstractCrawler {

    private static final Logger logger = LoggerFactory.getLogger(XuangubaoCrawler.class);

    @Override
    public String crawlHomeRequest(LocalCrawlerSession crawlerSession) {
        return crawlerSession.getCrawlerConfig().homePageRequest;
    }

    @Override
    public String crawlNextPageRequest(LocalCrawlerSession crawlerSession) {
        return null;
    }

    @Override
    public String auth(LocalCrawlerSession crawlerSession, Object pageContent, String request) {
        return (String) pageContent;
    }
}
