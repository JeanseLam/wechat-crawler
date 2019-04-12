package com.lin.crawler.common.httpclient;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 *
 * 创建HttpClient.
 *
 * Created by linjinzhi on 2018/12/11.
 */
public class HttpClientUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    public static CloseableHttpClient buildHttpClient(CookieStore cookieStore) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        //设置cookieStore
        setCookiesStore(cookieStore, httpClientBuilder);

        return httpClientBuilder.build();
    }


    /**
     * 用于本地调试.
     * @param cookieStore Http Cookies
     * @return CloseableHttpClient
     */
    public static CloseableHttpClient buildFiddlerProxyHttpClient(CookieStore cookieStore) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        //设置代理
        setFiddlerProxy(cookieStore, httpClientBuilder);
        return httpClientBuilder.build();
    }


    public static CloseableHttpClient buildProxyHttpClient(CookieStore cookieStore) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        return httpClientBuilder.build();
    }


    public static CloseableHttpClient buildProxyHttpClient() {

        return buildProxyHttpClient(null);
    }

    /**
     * 获取可以绕过ssl 安全证书的CloseableHttpClient
     *
     * @param cookieStore cookieStore
     * @param useProxy    是否需要代理
     * @return
     */
    public static CloseableHttpClient buildSSLHttpClient(CookieStore cookieStore, boolean useProxy) {

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        setCookiesStore(cookieStore, httpClientBuilder);

        //绕过证书验证
        setSSL(httpClientBuilder);
        //设置代理
        if (useProxy) {
            // 获取代理ip和端口, 暂未建成代理服务
            String ip = "";
            int port = 0;
            setProxy(cookieStore, httpClientBuilder, ip, port);
        }

        return httpClientBuilder.build();
    }

    /**
     * 设置代理
     * @param cookieStore
     * @param httpClientBuilder
     * @return
     */
    private static void setProxy(CookieStore cookieStore, HttpClientBuilder httpClientBuilder, String ip, int port) {

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        //设置代理IP、端口
        HttpHost httpHost = new HttpHost(ip, port, "http");
        //把代理设置到请求配置
        RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).build();

        httpClientBuilder.setDefaultRequestConfig(requestConfig);

        //设置代理账号验证
        String userName = "******";
        String pas = "******";
        credsProvider.setCredentials(new AuthScope(ip, port), new UsernamePasswordCredentials(userName, pas));

        httpClientBuilder.setDefaultCredentialsProvider(credsProvider);

        //设置cookieStore
        setCookiesStore(cookieStore, httpClientBuilder);
    }



    private static boolean setFiddlerProxy(CookieStore cookieStore, HttpClientBuilder httpClientBuilder) {

        //设置代理IP、端口
        HttpHost httpHost = new HttpHost("127.0.0.1", 8888, "http");
        //把代理设置到请求配置
        RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).build();

        httpClientBuilder.setDefaultRequestConfig(requestConfig);

        //设置cookieStore
        setCookiesStore(cookieStore, httpClientBuilder);
        return true;
    }



    /**
     * 设置cookieStore
     *
     * @param cookieStore
     * @param httpClientBuilder
     */
    private static void setCookiesStore(CookieStore cookieStore, HttpClientBuilder httpClientBuilder) {
        if (cookieStore != null) {
            httpClientBuilder.setDefaultCookieStore(cookieStore);
        }
    }

    /**
     * 获取绕过ssl  证书验证的SSLContext
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static SSLContext getSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        //用于绕过验证，
        TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sslContext.init(null, new TrustManager[]{trustManager}, null);
        return sslContext;
    }


    /**
     * 绕过证书验证
     *
     * @param httpClientBuilder
     */
    private static void setSSL(HttpClientBuilder httpClientBuilder) {
        try {
            //采用绕过验证的方式处理https请求
            SSLContext sslcontext = getSSLContext();
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
    }
}
