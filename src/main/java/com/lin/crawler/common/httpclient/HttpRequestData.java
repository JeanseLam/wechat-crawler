package com.lin.crawler.common.httpclient;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.Serializable;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;


/**
 *
 * 封装HttpClient及其周边.
 *
 * Created by linjinzhi on 2018-12-11.
 *
 */
public class HttpRequestData implements Serializable {

    private static final long serialVersionUID = -3942064259653141609L;

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestData.class);

    private transient CloseableHttpClient closeableHttpClient;

    private BasicCookieStore cookieStore;

    public HttpRequestData() {
        this.cookieStore = new BasicCookieStore();
        this.closeableHttpClient = HttpClientUtils.buildSSLHttpClient(cookieStore, false);
    }

    public HttpRequestData(boolean useProxy, BasicCookieStore cookieStore) {
        if(cookieStore == null) {
            this.cookieStore = new BasicCookieStore();
        } else {
            this.cookieStore = cookieStore;
        }
        this.closeableHttpClient = HttpClientUtils.buildSSLHttpClient(cookieStore, useProxy);
    }

    public HttpRequestData(String ip, int port, BasicCookieStore cookieStore, boolean auth) {

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        // 绕过ssl
        try {
            //采用绕过验证的方式处理https请求
            SSLContext sslcontext = HttpClientUtils.getSSLContext();
            // 设置协议http和https对应的处理socket链接工厂的对象
            Registry<ConnectionSocketFactory> registry = RegistryBuilder
                    .<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext))
                    .build();
            HttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(registry);

            httpClientBuilder.setConnectionManager(manager);
        } catch (NoSuchAlgorithmException e) {
            logger.error("buildSSLHttpClient exception. message:{} ; exception:{}.", e.getMessage(), e);
        } catch (KeyManagementException e) {
            logger.error("buildSSLHttpClient exception. message:{} ; exception:{}.", e.getMessage(), e);
        }

        // 设置代理
        CredentialsProvider credsProvider = new BasicCredentialsProvider();

        if (StringUtils.isNotBlank(ip) && port > 0) {
            //设置代理IP、端口
            HttpHost httpHost = new HttpHost(ip, port, "http");
            //把代理设置到请求配置
            RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).build();

            httpClientBuilder.setDefaultRequestConfig(requestConfig);

            //设置代理账号验证
            if(auth) {
                String userName = "******";
                String pas = "iek*(202KJ";
                credsProvider.setCredentials(new AuthScope(ip, port), new UsernamePasswordCredentials(userName, pas));

                httpClientBuilder.setDefaultCredentialsProvider(credsProvider);
            }

            //设置cookieStore
            if(cookieStore != null) {
                this.cookieStore = cookieStore;
                httpClientBuilder.setDefaultCookieStore(this.cookieStore);
            } else {
                this.cookieStore = new BasicCookieStore();
                httpClientBuilder.setDefaultCookieStore(this.cookieStore);
            }
        }

        this.closeableHttpClient = httpClientBuilder.build();
    }

    public HttpRequestData(BasicCookieStore cookieStore) {
        if(cookieStore == null) {
            this.cookieStore = new BasicCookieStore();
        } else {
            this.cookieStore = cookieStore;
        }
        this.closeableHttpClient = HttpClientUtils.buildSSLHttpClient(this.cookieStore, true);
    }

    public void closeHttpClient() {
        try {
            if(this.closeableHttpClient != null) {
                this.closeableHttpClient.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 切换代理，同时Cookies全部清空
     */
    public void switchHttpProxy() {
        this.cookieStore = new BasicCookieStore();
        this.closeableHttpClient = HttpClientUtils.buildSSLHttpClient(cookieStore, true);
    }

    public void addCookie(String domain, String name, String value, Date expires, String path) {
        if(StringUtils.isBlank(domain) || StringUtils.isBlank(name) || StringUtils.isBlank(value)) {
            logger.error("invalid cookie, domain:{}, name:{}, value:{}", domain, name, value);
            return;
        }

        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain(domain);
        cookie.setExpiryDate(expires);
        cookie.setPath(path);
        cookieStore.addCookie(cookie);
    }

    public void addCookie(String domain, String name, String value) {
        if(StringUtils.isBlank(name) || StringUtils.isBlank(value)) {
            logger.error("invalid cookie, domain:{}, name:{}, value:{}", domain, name, value);
            return;
        }
        BasicClientCookie cookie = new BasicClientCookie(name, value);

        if(StringUtils.isNotBlank(domain)) {
            cookie.setDomain(domain);
        }

        cookieStore.addCookie(cookie);
    }

    public void removeCookie(String name) {
        if(StringUtils.isBlank(name)) {
            logger.error("invalid cookie name:{}", name);
            return;
        }

        List<Cookie> cookies = cookieStore.getCookies();
        for(int i = 0; i < cookies.size(); i++) {
            if(cookies.get(i).getName().equals(name)) {
                cookies.remove(i);
                i--;
            }
        }

        cookieStore.clear();
        Cookie[] copyCookies = new Cookie[cookies.size()];
        cookies.toArray(copyCookies);
        cookieStore.addCookies(copyCookies);
    }


    public void displayCookies() {
        if(cookieStore != null) {
            logger.info("========================== cookies ========================");
            List<Cookie> cookies = cookieStore.getCookies();
            for(Cookie cookie : cookies) {
                logger.info("domain:{}, name:{}, value:{}", cookie.getDomain(), cookie.getName(), cookie.getValue());
            }
            logger.info("========================== cookies ========================");
        }
    }

    public String getCookieValue(String name) {
        List<Cookie> cookies = cookieStore.getCookies();
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public CloseableHttpClient getCloseableHttpClient() {
        return closeableHttpClient;
    }

    public void setCloseableHttpClient(CloseableHttpClient closeableHttpClient) {
        this.closeableHttpClient = closeableHttpClient;
    }

    public BasicCookieStore getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(BasicCookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }
}
