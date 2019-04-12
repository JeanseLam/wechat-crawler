package com.lin.crawler;

import com.lin.crawler.context.CrawlerContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlerContextTest {

    @Resource
    private CrawlerContext crawlerContext;

    @Test
    public void test() {
        crawlerContext.init();
        crawlerContext.run();
    }
}
