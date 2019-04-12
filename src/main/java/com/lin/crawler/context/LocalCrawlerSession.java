package com.lin.crawler.context;

import com.lin.crawler.common.entity.NewsArticle;
import com.lin.crawler.common.httpclient.HttpRequestData;
import org.apache.http.impl.client.BasicCookieStore;

import java.util.*;

/**
 *
 * Created by linjinzhi on 2019/04/12.
 *
 * 本地爬虫会话信息保存
 *
 */
public class LocalCrawlerSession {

    private List<NewsArticle> articles = new ArrayList<>();

    private BasicCookieStore cookieStore;

    private HttpRequestData httpRequestData;

    private CrawlerConfig crawlerConfig;

    private Queue<String> articleListUrls = new LinkedList<>();

    private Map<String, Object> payload = new HashMap<>();

    /**
     * 在抓取过程中可能需要预设请求头, 如果设置了请求头将使用设置的请求头发起请求
     */
    private Map<String, String> requestHeaders = new HashMap<>();


    public List<NewsArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<NewsArticle> articles) {
        this.articles = articles;
    }

    public BasicCookieStore getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(BasicCookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    public HttpRequestData getHttpRequestData() {
        return httpRequestData;
    }

    public void setHttpRequestData(HttpRequestData httpRequestData) {
        this.httpRequestData = httpRequestData;
    }

    public CrawlerConfig getCrawlerConfig() {
        return crawlerConfig;
    }

    public void setCrawlerConfig(CrawlerConfig crawlerConfig) {
        this.crawlerConfig = crawlerConfig;
    }

    public Queue<String> getArticleListUrls() {
        return articleListUrls;
    }

    public void setArticleListUrls(Queue<String> articleListUrls) {
        this.articleListUrls = articleListUrls;
    }


    public void putData(String key, Object value) {
        this.payload.put(key, value);
    }


    public Object getData(String key) {
        return this.payload.get(key);
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }
}
