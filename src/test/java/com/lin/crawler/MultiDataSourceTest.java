package com.lin.crawler;

import com.lin.crawler.dao.ButterflyDao;
import com.lin.crawler.dao.NewsArticleDao;
import com.lin.crawler.dao.TagDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MultiDataSourceTest {

    @Resource
    private ButterflyDao butterflyDao;

    @Resource
    private NewsArticleDao newsArticleDao;

    @Resource
    private TagDao tagDao;

    @Test
    public void test() {

        System.out.println(butterflyDao != null && newsArticleDao != null && tagDao != null);
    }

}
