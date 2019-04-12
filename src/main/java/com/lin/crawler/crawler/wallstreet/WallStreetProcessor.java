package com.lin.crawler.crawler.wallstreet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lin.crawler.common.entity.NewsArticle;
import com.lin.crawler.context.AbstractProcessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by linjinzhi 2018-12-17
 *
 * 华尔街见闻解析器.
 *
 */
public class WallStreetProcessor extends AbstractProcessor {

    private static final Logger logger = LoggerFactory.getLogger(WallStreetProcessor.class);

    @Override
    public List<NewsArticle> processListPage(Object pageContent) {

        List<NewsArticle> articles = new ArrayList<>();
        String html = (String) pageContent;
        try {
            Document document = Jsoup.parse(html);
            Elements elements = document.getElementsByTag("script");
            for(Element element : elements) {
                if(element.select("script").size() == 1 && element.toString().contains("__IVANKA_API_CACHE__")) {
                    String json = element.toString();
                    json = json.substring(json.indexOf('{'), json.lastIndexOf('}') + 1);

                    JSONObject jsonObject = JSON.parseObject(json);
                    jsonObject = jsonObject.getJSONObject("cachedResponse");
                    jsonObject = jsonObject.getJSONObject("[\"/content/fabricate-articles\",{\"channel\":\"global\",\"accept\":\"article\",\"cursor\":\"\",\"limit\":20}]");
                    JSONArray jsonArray = jsonObject.getJSONObject("value").getJSONArray("items");

                    for(int i = 0; i < jsonArray.size(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        String title = jsonObject.getJSONObject("resource").getString("title");
                        String abstractText = jsonObject.getJSONObject("resource").getString("content_short");
                        String detailLink = jsonObject.getJSONObject("resource").getString("uri");
                        String imageLink = jsonObject.getJSONObject("resource").getString("image_uri");
                        String author = jsonObject.getJSONObject("resource").getJSONObject("author").getString("display_name");

                        NewsArticle newsArticle = new NewsArticle();
                        newsArticle.setTitle(title);
                        newsArticle.setAbstractText(abstractText);
                        newsArticle.setDetailLink(detailLink);
                        newsArticle.setImageLink(imageLink);
                        newsArticle.setAuthor(author);

                        articles.add(newsArticle);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("parse article list occurs an error, message:{}, exception:{}", e.getMessage(), e);
        }
        return articles;
    }

    @Override
    public void processDetailPage(Object pageContent, NewsArticle newsArticle) {

        String json = (String) pageContent;
        try {
            JSONObject jsonObject = JSON.parseObject(json);
            String content = jsonObject.getJSONObject("data").getString("content");
            newsArticle.setContent(content);
        } catch (Exception e) {
            logger.error("parse article detail occurs an error, message:{}, exception:{}", e.getMessage(), e);
        }
    }
}
