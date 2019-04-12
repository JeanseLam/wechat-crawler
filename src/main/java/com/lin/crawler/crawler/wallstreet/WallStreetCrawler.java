package com.lin.crawler.crawler.wallstreet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lin.crawler.common.entity.NewsArticle;
import com.lin.crawler.common.httpclient.RequestUtils;
import com.lin.crawler.common.httpclient.HttpRequestData;
import com.lin.crawler.context.AbstractCrawler;
import com.lin.crawler.context.AbstractProcessor;
import com.lin.crawler.context.Crawler;
import com.lin.crawler.context.LocalCrawlerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by linjinzhi on 2018-12-17.
 *
 * 华尔街见闻爬虫。华尔街见闻会员文章要充费，需要用账号登陆再抓取.
 *
 */
@Component
@Crawler(source = "wallStreet")
public class WallStreetCrawler extends AbstractCrawler {

    private static final Logger logger = LoggerFactory.getLogger(WallStreetCrawler.class);

    private static final String LOGIN_ACCOUNT = "******0@qq.com";

    private static final String LOGIN_PASSWORD = "******";

    @Override
    public String crawlHomeRequest(LocalCrawlerSession crawlerSession) {

        HttpRequestData httpRequestData = crawlerSession.getHttpRequestData();
        Map<String, String> headers = new HashMap<>();
        try {
             headers.put("Host", "api-prod.wallstreetcn.com");
             headers.put("Connection", "keep-alive");
             headers.put("x-track-info", "{\"appId\":\"com.wallstreetcn.web\",\"appVersion\":\"0.9.5\"}");
             headers.put("Origin", "https://wallstreetcn.com");
             headers.put("x-client-type", "pc");
             headers.put("x-ivanka-platform", "wscn-platform");
             headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
             headers.put("content-type", "application/json");
             headers.put("Accept", "*/*");
             headers.put("Referer", "https://wallstreetcn.com/news/global");

            JSONObject params = JSON.parseObject("{\"app_type\":\"wscn\",\"signin_key\":\"" + LOGIN_ACCOUNT + "\"," +
                    "\"signin_value\":\"" + LOGIN_PASSWORD + "\",\"refresh_token\":true}");

            String loginResponse = RequestUtils.postJson(httpRequestData, "https://api-prod.wallstreetcn.com/apiv1/user/signin/password", params, null, headers);

            int code = JSON.parseObject(loginResponse).getInteger("code");
            if(code == 20000) {
                logger.info("login wall street success");
                String token = JSON.parseObject(loginResponse).getJSONObject("data").getString("token");
                crawlerSession.putData("token", token);

            } else {
                logger.error("login wall street fail");
            }

        } catch (Exception e) {
            logger.error("login wall street fail, message:{}, exception:{}", e.getMessage(), e);
        }

        return crawlerSession.getCrawlerConfig().homePageRequest;
    }


    /**
     * 华尔街见闻请求文章详情需要得到登陆成功返回的token作为请求头，覆盖父类的抓取方法.
     * @param crawlerSession 爬虫会话数据封装
     */
    @Override
    public void crawlArticleDetail(LocalCrawlerSession crawlerSession) {

        List<NewsArticle> articles = crawlerSession.getArticles();
        HttpRequestData httpRequestData = crawlerSession.getHttpRequestData();
        String token = (String) crawlerSession.getData("token");
        Map<String, String> headers = new HashMap<>();

        for(NewsArticle newsArticle : articles) {
            try {
                headers.put("Host", "api-prod.wallstreetcn.com");
                headers.put("Connection", "keep-alive");
                headers.put("x-ivanka-token", token);
                headers.put("x-track-info", "{\"appId\":\"com.wallstreetcn.web\",\"appVersion\":\"0.9.7\"}");
                headers.put("Origin", "https://wallstreetcn.com");
                headers.put("x-client-type", "pc");
                headers.put("x-ivanka-platform", "wscn-platform");
                headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
                headers.put("Accept", "*/*");
                headers.put("Referer", newsArticle.getDetailLink());
                headers.put("Accept-Language", "zh-CN,zh;q=0.9");

                String articleId = newsArticle.getDetailLink().substring(newsArticle.getDetailLink().lastIndexOf('/') + 1);
                String html = RequestUtils.get(httpRequestData, "https://api-prod.wallstreetcn.com/apiv1/content/articles/" + articleId +
                        "?extract=0&accept_theme=theme%2Cpremium-theme", null, headers);

                AbstractProcessor processor = getProcessor();
                processor.processDetailPage(html, newsArticle);

                newsArticle.setSource(crawlerSession.getCrawlerConfig().source);
                newsArticle.setSourceType(crawlerSession.getCrawlerConfig().sourceType);
                newsArticle.setTopic(crawlerSession.getCrawlerConfig().topic);

            } catch (Exception e) {
                logger.error("wall street crawler crawl article detail occurs an error, message:{}, exception:{}", e.getMessage(), e);
            }
        }
    }

    @Override
    public String crawlNextPageRequest(LocalCrawlerSession crawlerSession) {
        // 华尔街见闻下一页是通过前一页的next_cursor作为参数请求下一页的数据
        return null;
    }

    @Override
    public String auth(LocalCrawlerSession crawlerSession, Object pageContent, String request) {
        return (String) pageContent;
    }
}
