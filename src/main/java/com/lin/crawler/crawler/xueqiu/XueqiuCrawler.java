package com.lin.crawler.crawler.xueqiu;

import com.lin.crawler.context.AbstractCrawler;
import com.lin.crawler.context.Crawler;
import com.lin.crawler.context.LocalCrawlerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * Created by linjinzhi on 2018/12/25
 *
 * 雪球资讯爬虫。
 *
 */
@Component
@Crawler(source = "xueqiu")
public class XueqiuCrawler extends AbstractCrawler {

    private static final Logger logger = LoggerFactory.getLogger(XueqiuCrawler.class);

    @Override
    public String crawlHomeRequest(LocalCrawlerSession crawlerSession) {
        return null;
    }

    @Override
    public String crawlNextPageRequest(LocalCrawlerSession crawlerSession) {
        return null;
    }

    @Override
    public String auth(LocalCrawlerSession crawlerSession, Object pageContent, String request) {
        return null;
    }
}
