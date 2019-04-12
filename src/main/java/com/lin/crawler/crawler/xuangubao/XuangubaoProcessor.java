package com.lin.crawler.crawler.xuangubao;

import com.lin.crawler.common.entity.NewsArticle;
import com.lin.crawler.context.AbstractProcessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by linjinzhi on 2018-12-17
 *
 * 选股宝解析器.
 *
 */
public class XuangubaoProcessor extends AbstractProcessor {

    private static final Logger logger = LoggerFactory.getLogger(XuangubaoProcessor.class);

    @Override
    public List<NewsArticle> processListPage(Object pageContent) {

        List<NewsArticle> articles = new ArrayList<>();

        String html = (String) pageContent;
        Document document = Jsoup.parse(html);
        Elements elements = document.select( "#original-subj-list").first().select("li");

        for(Element element : elements) {
            try {

                if(element.attr("class").equals("stock-group-item")) {
                    continue;
                }

                String title = element.select("a").select("span").text().trim();
                String abstractText = "";
                Elements abstractTextElement = element.select("div").get(1).select("p");
                if(abstractTextElement != null && abstractTextElement.size() != 0) {
                    abstractText = abstractTextElement.first().text().trim();
                }
                String imageLink = element.select("div").get(0).attr("style");
                imageLink = imageLink.substring(imageLink.indexOf('(') + 1, imageLink.lastIndexOf(')'));
                String detailLink = element.select("div").get(1).select("a").first().attr("href");
                detailLink = MessageFormat.format("https://xuangubao.cn{0}", detailLink);

                NewsArticle article = new NewsArticle();
                article.setTitle(title);
                article.setAbstractText(abstractText);
                article.setImageLink(imageLink);
                article.setDetailLink(detailLink);

                articles.add(article);
            } catch (Exception e) {
                logger.error("parse list article occurs an error, message:{}, exception:{}", e.getMessage(), e);
            }
        }
        return articles;
    }

    @Override
    public void processDetailPage(Object pageContent, NewsArticle newsArticle) {

        String html = (String) pageContent;
        try {
            Document document = Jsoup.parse(html);
            Element element = document.select("#nuxt-layout-container > section > div > article").first();
            Elements elements = element.select("p");
            for(int i = 0; i < elements.size(); i++) {
                Element p = elements.get(i);
                if(p.select("p").size() == 1 && p.toString().contains("更多资讯请下载")) {
                    elements.remove(i);
                    i--;
                }
            }
            String content = element.html();
            newsArticle.setContent(content);
        } catch (Exception e) {
            logger.error("parse article detail occurs an error, message:{}, exception:{}", e.getMessage(), e);
        }
    }
}
