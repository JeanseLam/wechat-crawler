package com.lin.crawler;

import com.lin.crawler.business.NewsArticleService;
import com.lin.crawler.common.entity.MyUserArticle;
import com.lin.crawler.common.entity.NewsArticle;
import com.lin.crawler.dao.ButterflyDao;
import com.lin.crawler.dao.NewsArticleDao;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NewsArticleServiceTest {

    @Resource
    private NewsArticleService newsArticleService;

    @Resource
    private NewsArticleDao newsArticleDao;

    @Resource
    private ButterflyDao butterflyDao;

    @Test
    public void testCrawlArticleConvertToUserArticle() {

        List<NewsArticle> crawlArticles = newsArticleDao.queryArticlesByMinId(103L);
        List<MyUserArticle> userArticles = new ArrayList<>();
        newsArticleService.convertCrawlArticleToUserArticle(crawlArticles, userArticles);

        System.out.println("打标成功数：" + userArticles.size());

        butterflyDao.batchInsert(userArticles);
    }


    @Test
    public void testConvertTag() {

        List<NewsArticle> crawlArticles = newsArticleDao.queryArticlesByMinId(1736L);
        newsArticleService.convertCrawlArticleToUserArticle(crawlArticles);

        Scanner scan = new Scanner(System.in);
        scan.next();
    }


    @Test
    public void testInsert() {

        List<NewsArticle> crawlArticles = newsArticleDao.queryArticlesByMinId(103L);
        NewsArticle article = crawlArticles.get(0);
        article.setTitle("test_" + UUID.randomUUID().toString().replaceAll("-", ""));

        List<NewsArticle> tmpList = ImmutableList.of(article);
        Set<Long> ids = newsArticleService.add(tmpList);
        for(long id : ids) {
            System.out.println(id);
        }
    }
}
