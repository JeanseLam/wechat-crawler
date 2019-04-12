package com.lin.crawler.crawler.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lin.crawler.common.EmojiFilter;
import com.lin.crawler.common.entity.NewsArticle;
import com.lin.crawler.context.AbstractProcessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WechatProcessor extends AbstractProcessor {

    private static final Logger logger= LoggerFactory.getLogger(WechatProcessor.class);

    @Override
    public List<NewsArticle> processListPage(Object pageContent) {

        List<NewsArticle> articles = new ArrayList<>();

        try {
            String html = (String) pageContent;
            Document document= Jsoup.parse(html);
            Elements elements = document.getElementsByTag("script");

            for(Element element : elements) {
                if(element.select("script").size() == 1 && element.toString().contains("msgList")) {
                    String json = element.toString();
                    json = json.substring(json.indexOf('{'), json.lastIndexOf('}') + 1);
                    JSONObject jsonObject = JSON.parseObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("list");

                    for(int i = 0; i < jsonArray.size(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        object = object.getJSONObject("app_msg_ext_info");

                        String title = object.getString("title");
                        String author= object.getString("author");
                        String abstractText = object.getString("digest");
                        String imageLink = object.getString("cover");
                        String detailLink = object.getString("content_url");
                        detailLink= detailLink.replaceAll("amp;", "");

                        NewsArticle newsArticle = new NewsArticle();
                        newsArticle.setTitle(title);
                        newsArticle.setAuthor(author);
                        newsArticle.setAbstractText(abstractText);
                        newsArticle.setImageLink(imageLink);
                        newsArticle.setDetailLink("https://mp.weixin.qq.com" + detailLink);
                        newsArticle.setCrawlTime(new Date());
                        articles.add(newsArticle);

                        // 嵌套文章
                        JSONArray multiAppMsgItemList = object.getJSONArray("multi_app_msg_item_list");
                        if(multiAppMsgItemList != null && !multiAppMsgItemList.isEmpty()) {
                            for(int j = 0; j < multiAppMsgItemList.size(); j++) {
                                JSONObject mulObject = multiAppMsgItemList.getJSONObject(j);

                                title = mulObject.getString("title");
                                author= mulObject.getString("author");
                                abstractText = mulObject.getString("digest");
                                imageLink = mulObject.getString("cover");
                                detailLink = mulObject.getString("content_url");
                                detailLink= detailLink.replaceAll("amp;", "");

                                NewsArticle mulNewsArticle = new NewsArticle();
                                mulNewsArticle.setTitle(title);
                                mulNewsArticle.setAuthor(author);
                                mulNewsArticle.setAbstractText(abstractText);
                                mulNewsArticle.setImageLink(imageLink);
                                mulNewsArticle.setDetailLink("https://mp.weixin.qq.com" + detailLink);
                                mulNewsArticle.setCrawlTime(new Date());
                                articles.add(mulNewsArticle);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("parse article list occurs error, message:{}, exception:{}", e.getMessage(), e);
        }

        return articles;
    }

    @Override
    public void processDetailPage(Object pageContent, NewsArticle newsArticle) {

        String html = (String) pageContent;
        try {
            Document document = Jsoup.parse(html);

            // 微信公众号数据清洗
            WechatDataCleanUtils.wechatDataClean(newsArticle.getTopic(), document);

            String htmlContent = document.select("#js_content").toString();
            htmlContent = WechatDataCleanUtils.onlyP(htmlContent);
            newsArticle.setContent(EmojiFilter.filterEmoji(htmlContent));
        } catch (Exception e) {
            logger.error("parse article detail content occurs an error, message:{}, exception:{}", e.getMessage(), e);
        }
    }
}
