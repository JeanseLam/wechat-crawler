package com.lin.crawler;

import com.alibaba.fastjson.JSON;
import com.lin.crawler.common.entity.MyUserArticle;
import com.lin.crawler.common.entity.NewsArticle;
import com.lin.crawler.common.file.FileServerHelper;
import com.lin.crawler.context.AbstractProcessor;
import com.lin.crawler.context.LocalCrawlerSession;
import com.lin.crawler.crawler.wechat.WechatDataCleanUtils;
import com.lin.crawler.crawler.wechat.WechatProcessor;
import com.lin.crawler.dao.ButterflyDao;
import com.lin.crawler.dao.NewsArticleDao;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataCleanTest {

    @Resource
    private NewsArticleDao newsArticleDao;

    @Resource
    private FileServerHelper fileServerHelper;

    @Resource
    private ButterflyDao butterflyDao;

    @Test
    public void testWechatDataClean() throws IOException {
        List<NewsArticle> articles = newsArticleDao.queryArticlesByMinId(300L);

        for(NewsArticle newsArticle : articles) {
            String content = newsArticle.getContent();
            Document document = Jsoup.parse(content);

            // 去掉所有超链接
            Elements elements = document.getElementsByTag("a");
            for (Element element : elements) {
                element.attr("href", "");
                element.text("");
            }

            // 图片下载和替换
            elements = document.getElementsByTag("img");
            for(Element element : elements) {

                String dataSrc = element.attr("data-src");
                String src = element.attr("src");

                if(StringUtils.isNotBlank(dataSrc) && StringUtils.isBlank(src)) {
                    String link = fileServerHelper.upload(dataSrc);
                    element.attr("data-src", link);
                    element.attr("src", link);

                } else if(StringUtils.isBlank(dataSrc) && StringUtils.isNotBlank(src)) {
                    String link = fileServerHelper.upload(src);
                    element.attr("src", link);

                } else if(StringUtils.isNotBlank(dataSrc) && StringUtils.isNotBlank(src)) {
                    String dataSrcLink = fileServerHelper.upload(dataSrc);
                    String srcLink = fileServerHelper.upload(src);
                    element.attr("data-src", dataSrc);
                    element.attr("src", srcLink);

                } else {
                    // 图片不是通过data-src和src属性来设置，记录下来
                    System.out.println("image is not set into attribute data-src or src");
                    System.out.println(element.html());
                }
            }

            // 替换原来的文本
            newsArticle.setContent(document.html());
        }
        System.out.println(JSON.toJSONString(articles));
        System.out.println("data clean finish");
    }


    @Test
    public void testDataClean() {
        List<NewsArticle> articles = newsArticleDao.queryArticlesByMinId(742L);
        for(NewsArticle newsArticle : articles) {
            String content = newsArticle.getContent();
            Document document = Jsoup.parse(content);
            WechatDataCleanUtils.wechatDataClean(newsArticle.getTopic(), document);
            newsArticle.setContent(document.html());
            newsArticle.setUpdateTime(new Date());
        }
    }


    @Test
    public void testDataClean1() {
        try {
            String html = IOUtils.toString(new FileReader(new File("C:\\Users\\admin\\Desktop\\test.html")));
            AbstractProcessor processor = new WechatProcessor();
            NewsArticle newsArticle = new NewsArticle();
            newsArticle.setTopic("正解局");
            processor.processDetailPage(html, newsArticle);

            LocalCrawlerSession session = new LocalCrawlerSession();
            List<NewsArticle> articles = new ArrayList<>();
            articles.add(newsArticle);
            session.setArticles(articles);
            processor.processImageLink(session);
            System.out.println(newsArticle.getContent());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testCleanBlankLine() {

        try {
            String html = IOUtils.toString(new FileReader(new File("C:\\Users\\admin\\Desktop\\test.html")));
            Document document = Jsoup.parse(html);
            WechatDataCleanUtils.wechatDataClean("正解局", document);
            System.out.println(document.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testCleanLastImgAndText() {

        try {
            List<MyUserArticle> articles = butterflyDao.getArticleByMinId(2121L);
            for(MyUserArticle myUserArticle : articles) {
                String content = myUserArticle.getContent();
                Document document = Jsoup.parse(content);

                Elements elements = document.getElementsByTag("img");
                if(elements != null && !elements.isEmpty()) {
                    elements.last().remove();
                }

                elements = document.getElementsByTag("p");
                for(int i = elements.size() - 1; i >= 0; i--) {
                    Element element = elements.get(i);
                    if(element.hasText()) {
                        element.remove();
                        break;
                    }
                }

                myUserArticle.setContent(document.toString());
            }

            butterflyDao.batchUpdate(articles);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
