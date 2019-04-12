package com.lin.crawler;

import com.lin.crawler.context.CrawlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 *
 * Created by linjinzhi on 2018-12-12.
 *
 * 应用启动入口
 *
 */
@SpringBootApplication
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        try {
            ApplicationContext applicationContext = SpringApplication.run(App.class);
            CrawlerContext crawlerContext = applicationContext.getBean(CrawlerContext.class);
            crawlerContext.init();
            crawlerContext.run();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
