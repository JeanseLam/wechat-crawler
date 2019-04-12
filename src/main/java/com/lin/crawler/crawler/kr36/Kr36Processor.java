package com.lin.crawler.crawler.kr36;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lin.crawler.common.entity.NewsArticle;
import com.lin.crawler.common.httpclient.RequestUtils;
import com.lin.crawler.common.httpclient.HttpRequestData;
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
 * Created by linjinzhi on 2018-12-17.
 *
 * 36氪解析器.
 *
 */
public class Kr36Processor extends AbstractProcessor {

    private static final Logger logger = LoggerFactory.getLogger(Kr36Processor.class);

    @Override
    public List<NewsArticle> processListPage(Object pageContent) {

        List<NewsArticle> articles = new ArrayList<>();

        String html = (String) pageContent;
        try {
            Document document = Jsoup.parse(html);
            Elements elements = document.getElementsByTag("script");
            for(Element element : elements) {
                if(element.select("script").size() == 1 && element.toString().contains("var props={")) {
                    String json = element.toString();
                    json = json.substring(json.indexOf('{'), json.indexOf("locationnal=") - 1);
                    JSONObject jsonObject = JSON.parseObject(json);

                    // 解析hotPosts|hotPost
                    JSONArray jsonArray = jsonObject.getJSONArray("hotPosts|hotPost");
                    for(int i = 0; i < jsonArray.size(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        NewsArticle article = parseListItem(object);
                        articles.add(article);
                    }

                    // 解析feedPostsLatest|post
                    jsonArray = jsonObject.getJSONArray("feedPostsLatest|post");
                    for(int i = 0; i < jsonArray.size(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        NewsArticle article = parseListItem(object);
                        articles.add(article);
                    }

                    // 解析projectNews|post
                    jsonArray = jsonObject.getJSONArray("projectNews|post");
                    for(int i = 0; i < jsonArray.size(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        NewsArticle article = parseListItem(object);
                        articles.add(article);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("parse 36kr article list occurs an error, message :{}, exception:{}", e.getMessage(), e);
        }
        return articles;
    }

    @Override
    public void processDetailPage(Object pageContent, NewsArticle newsArticle) {

        String html = (String) pageContent;
        try {
            Document document = Jsoup.parse(html);
            Elements elements = document.getElementsByTag("script");
            for(Element element : elements) {
                if(element.select("script").size() == 1 && element.toString().contains("var props=")) {
                    String json = element.toString();
                    json = json.substring(json.indexOf('{'), json.indexOf("locationnal=") - 1);

                    JSONObject jsonObject = JSON.parseObject(json).getJSONObject("detailArticle|post");
                    String authorId = jsonObject.getString("user_id");
                    String content = jsonObject.getString("content");

                    newsArticle.setContent(content);
                    newsArticle.setAuthor(getAuthorById(authorId));
                }
            }
        } catch (Exception e) {
            logger.error("parse article detail page occurs an error, message:{}, exception:{}", e.getMessage(), e);
        }
    }


    private NewsArticle parseListItem(JSONObject object) {
        String title = object.getString("title");
        String abstractText = object.getString("summary");
        String imageLink = object.getString("cover");
        String id = object.getString("id");
        String detailLink = MessageFormat.format("https://36kr.com/p/{0}.html", id);

        NewsArticle article = new NewsArticle();
        article.setTitle(title);
        article.setAbstractText(abstractText);
        article.setImageLink(imageLink);
        article.setDetailLink(detailLink);
        return article;
    }


    private String getAuthorById(String userId) {
        try {
            HttpRequestData httpRequestData = new HttpRequestData();
            String html = RequestUtils.get(httpRequestData, "https://36kr.com/user/" + userId);
            Document document = Jsoup.parse(html);
            Element element = document.select("title").first();
            String author = element.text();
            author = author.substring(0, author.lastIndexOf('的'));
            return author;
        } catch (Exception e) {
            logger.error("crawl author name fail, message:{}, exception:{}", e.getMessage(), e);
        }
        return "";
    }
}
