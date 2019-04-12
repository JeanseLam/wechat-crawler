package com.lin.crawler.common.httpclient;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.HeaderGroup;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Http请求工具。
 * <p>
 * Modified by linjinzhi on 2018/12/11.
 */
public class RequestUtils {

    private static final Logger logger = LoggerFactory.getLogger(RequestUtils.class);

    //默认的编码
    public final static String DEFAOUT_CHARSET = "UTF-8";
    private final static String DEFAOUT_CONTENTTYPE = "text/html";
    private final static Map<String, String> DEAULT_HEAHDERMAP = new HashMap<>();

    private final static RequestConfig GLOBAL_CONFIG = RequestConfig.custom().setCookieSpec(CookieSpecs.DEFAULT).build();

    static {
        //支持压缩
        DEAULT_HEAHDERMAP.put("Accept-Encoding", "gzip");
        DEAULT_HEAHDERMAP.put("Accept", "text/html, application/xhtml+xml, image/jxr, */*");
        DEAULT_HEAHDERMAP.put("Accept-Language", "zh-Hans-CN,zh-Hans;q=0.5");
        DEAULT_HEAHDERMAP.put("Content-Type", "application/x-www-form-urlencoded");
        //chrome
        DEAULT_HEAHDERMAP.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
    }

    public static String get(HttpRequestData httpRequestData, String url, Map<String, String> inputs) throws IOException {
        url = getInputsUrl(url, inputs);
        return get(httpRequestData, url, null, null);
    }

    public static String get(HttpRequestData httpRequestData, String url) throws IOException {
        return get(httpRequestData, url, null, null);
    }

    private static String getInputsUrl(String url, Map<String, String> inputs) throws UnsupportedEncodingException {
        if (null != inputs) {
            StringBuilder urlBuilder = new StringBuilder(url);
            boolean first = true;
            // reconstitute the query, ready for appends
            if (!url.contains("?"))
                urlBuilder.append("?");
            for (Map.Entry<String, String> entry : inputs.entrySet()) {
                if (!first)
                    urlBuilder.append('&');
                else
                    first = false;
                urlBuilder.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(), DEFAOUT_CHARSET));
            }
            url = urlBuilder.toString();
        }
        return url;
    }


    public static String get(HttpRequestData httpRequestData, String url, RequestConfig requestConfig, Map<String, String> headers) throws IOException {
        String responseContent = null;
        HttpGet get = null;
        try {
            get = new HttpGet(url);
            if (null != requestConfig) {
                get.setConfig(requestConfig);
            }
            if (null == headers) {
                headers = DEAULT_HEAHDERMAP;
            } else {
                //如果请求头没有默认值，使用默认值
                for (Map.Entry<String, String> entry : DEAULT_HEAHDERMAP.entrySet()) {
                    if (!headers.containsKey(entry.getKey())) {
                        headers.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            HeaderGroup headerGroup = new HeaderGroup();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerGroup.addHeader(new BasicHeader(entry.getKey(), entry.getValue()));
            }
            get.setHeaders(headerGroup.getAllHeaders());
            HttpResponse response = httpRequestData.getCloseableHttpClient().execute(get);
            if (response.getEntity() == null) {
                return null;
            }

            responseContent = EntityUtils.toString(response.getEntity(), "UTF-8");
            // 确保实体内容被完全消耗
            EntityUtils.consumeQuietly(response.getEntity());
        } catch (ClientProtocolException e) {
            throw new ClientProtocolException(e);
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            if (null != get) {
                get.releaseConnection();
            }
        }
        return responseContent;
    }


    public static String get(HttpRequestData httpRequestData, String url, RequestConfig requestConfig, Map<String, String> headers, List<Header> responseHeaders) throws IOException {

        String responseContent = null;
        HttpGet get = null;
        try {
            get = new HttpGet(url);
            if (null != requestConfig) {
                get.setConfig(requestConfig);
            } else {
                get.setConfig(GLOBAL_CONFIG);
            }
            if (null == headers) {
                headers = DEAULT_HEAHDERMAP;
            } else {
                //如果请求头没有默认值，使用默认值
                for (Map.Entry<String, String> entry : DEAULT_HEAHDERMAP.entrySet()) {
                    if (!headers.containsKey(entry.getKey())) {
                        headers.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            HeaderGroup headerGroup = new HeaderGroup();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerGroup.addHeader(new BasicHeader(entry.getKey(), entry.getValue()));
            }
            get.setHeaders(headerGroup.getAllHeaders());
            HttpResponse response = httpRequestData.getCloseableHttpClient().execute(get);
            if (response.getEntity() == null) {
                return null;
            }

            // 设置头部
            responseHeaders.clear();
            responseHeaders.addAll(Arrays.asList(response.getAllHeaders()));

            logger.error("request url:{}, response header:{}", url, responseHeaders);

            responseContent = EntityUtils.toString(response.getEntity());
            // 确保实体内容被完全消耗
            EntityUtils.consumeQuietly(response.getEntity());
        } catch (ClientProtocolException e) {
            throw new ClientProtocolException(e);
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            if (null != get) {
                get.releaseConnection();
            }
        }
        return responseContent;
    }


    public static byte[] getBytes(HttpRequestData httpRequestData, String url, RequestConfig requestConfig, Map<String, String> headers) throws IOException {
        HttpResponse response = null;
        HttpGet get = null;
        try {
            get = new HttpGet(url);
            if (null != requestConfig) {
                get.setConfig(requestConfig);
            } else {
                get.setConfig(GLOBAL_CONFIG);
            }
            if (null == headers) {
                headers = DEAULT_HEAHDERMAP;
            } else {
                //如果请求头没有默认值，使用默认值
                for (Map.Entry<String, String> entry : DEAULT_HEAHDERMAP.entrySet()) {
                    if (!headers.containsKey(entry.getKey())) {
                        headers.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            HeaderGroup headerGroup = new HeaderGroup();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerGroup.addHeader(new BasicHeader(entry.getKey(), entry.getValue()));
            }
            get.setHeaders(headerGroup.getAllHeaders());
            response = httpRequestData.getCloseableHttpClient().execute(get);
            if (response == null) {
                return null;
            }
            return IOUtils.toByteArray(response.getEntity().getContent());
        } catch (ClientProtocolException e) {
            throw new ClientProtocolException(e);
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            if (null != get) {
                get.releaseConnection();
            }

            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        }
    }


    public static HttpResponse getResponse(HttpRequestData httpRequestData, String url, RequestConfig requestConfig, Map<String, String> headers) throws IOException {

        HttpResponse response = null;
        HttpGet get = null;
        try {
            get = new HttpGet(url);
            if (null != requestConfig) {
                get.setConfig(requestConfig);
            } else {
                get.setConfig(GLOBAL_CONFIG);
            }
            if (null == headers) {
                headers = DEAULT_HEAHDERMAP;
            } else {
                //如果请求头没有默认值，使用默认值
                for (Map.Entry<String, String> entry : DEAULT_HEAHDERMAP.entrySet()) {
                    if (!headers.containsKey(entry.getKey())) {
                        headers.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            HeaderGroup headerGroup = new HeaderGroup();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerGroup.addHeader(new BasicHeader(entry.getKey(), entry.getValue()));
            }
            get.setHeaders(headerGroup.getAllHeaders());
            response = httpRequestData.getCloseableHttpClient().execute(get);
            if (response == null) {
                return null;
            }
            EntityUtils.consumeQuietly(response.getEntity());
        } catch (ClientProtocolException e) {
            throw new ClientProtocolException(e);
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            if (null != get) {
                get.releaseConnection();
            }
        }
        return response;
    }


    public static String post(HttpRequestData httpRequestData, String url, Map<String, String> inputs) throws IOException {
        return post(httpRequestData, url, inputs, null, null);
    }

//    public static String post(HttpRequestData httpRequestData, String url) throws IOException {
//        return post(httpRequestData, url, new HashMap<>(), null, null);
//    }

    public static String post(HttpRequestData httpRequestData, String url, Map<String, String> inputs, Map<String, String> headers) throws IOException {
        return post(httpRequestData, url, inputs, null, headers);
    }

    public static String post(HttpRequestData httpRequestData, String url, String requst) throws IOException {
        return post(httpRequestData, url, new StringEntity(requst, "utf-8"), null, null);
    }

    public static String post(HttpRequestData httpRequestData, String url, String requst, Map<String, String> headers) throws IOException {
        return post(httpRequestData, url, new StringEntity(requst, "utf-8"), null, headers);
    }


    public static String post(HttpRequestData httpRequestData, String url, HttpEntity httpEntity, RequestConfig requestConfig, Map<String, String> headers) throws IOException {
        String responseContent = null;
        HttpPost post = null;
        try {
            post = new HttpPost(url);
            if (null != requestConfig) {
                post.setConfig(requestConfig);
            } else {
                post.setConfig(GLOBAL_CONFIG);
            }
            if (null == headers) {
                headers = DEAULT_HEAHDERMAP;
            } else {
                //如果请求头没有默认值，使用默认值
                for (Map.Entry<String, String> entry : DEAULT_HEAHDERMAP.entrySet()) {
                    if (!headers.containsKey(entry.getKey())) {
                        headers.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            HeaderGroup headerGroup = new HeaderGroup();
            headers.remove("Content-Type", "application/x-www-form-urlencoded");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerGroup.addHeader(new BasicHeader(entry.getKey(), entry.getValue()));
            }
            post.setHeaders(headerGroup.getAllHeaders());
            post.setEntity(httpEntity);
            HttpResponse response = httpRequestData.getCloseableHttpClient().execute(post);
            responseContent = EntityUtils.toString(response.getEntity());
            // 确保实体内容被完全消耗
            EntityUtils.consumeQuietly(response.getEntity());
        } catch (ClientProtocolException e) {
            throw new ClientProtocolException(e);
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            post.releaseConnection();
        }
        return responseContent;
    }


    public static String postJson(HttpRequestData httpRequestData, String url, JSONObject jsonObject, RequestConfig requestConfig, Map<String, String> headers) throws IOException {
        HttpPost post = null;
        String responseContent = null;
        HttpResponse response = null;
        try {
            post = new HttpPost(url);
            if (null != requestConfig) {
                post.setConfig(requestConfig);
            } else {
                post.setConfig(GLOBAL_CONFIG);
            }
            if (null == headers) {
                headers = DEAULT_HEAHDERMAP;
            } else {
                //如果请求头没有默认值，使用默认值
                for (Map.Entry<String, String> entry : DEAULT_HEAHDERMAP.entrySet()) {
                    if (!headers.containsKey(entry.getKey())) {
                        headers.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            HeaderGroup headerGroup = new HeaderGroup();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerGroup.addHeader(new BasicHeader(entry.getKey(), entry.getValue()));
            }
            post.setHeaders(headerGroup.getAllHeaders());
            post.setEntity(new StringEntity(jsonObject.toString(), "utf-8"));
            response = httpRequestData.getCloseableHttpClient().execute(post);
            responseContent = EntityUtils.toString(response.getEntity());
            // 确保实体内容被完全消耗
            EntityUtils.consumeQuietly(response.getEntity());
        } catch (ClientProtocolException e) {
            throw new ClientProtocolException(e);
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            post.releaseConnection();
        }
        return responseContent;
    }


    public static HttpResponse postResponse(HttpRequestData httpRequestData, String url, Map<String, String> inputs, RequestConfig requestConfig, Map<String, String> headers) throws IOException {
        HttpPost post = null;
        try {
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            if (null != inputs) {
                for (Map.Entry<String, String> entry : inputs.entrySet()) {
                    parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            post = new HttpPost(url);
            if (null != requestConfig) {
                post.setConfig(requestConfig);
            } else {
                post.setConfig(GLOBAL_CONFIG);
            }
            if (null == headers) {
                headers = DEAULT_HEAHDERMAP;
            } else {
                //如果请求头没有默认值，使用默认值
                for (Map.Entry<String, String> entry : DEAULT_HEAHDERMAP.entrySet()) {
                    if (!headers.containsKey(entry.getKey())) {
                        headers.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            HeaderGroup headerGroup = new HeaderGroup();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerGroup.addHeader(new BasicHeader(entry.getKey(), entry.getValue()));
            }
            post.setHeaders(headerGroup.getAllHeaders());
            post.setEntity(new UrlEncodedFormEntity(parameters, "utf-8"));
            return httpRequestData.getCloseableHttpClient().execute(post);
        } catch (ClientProtocolException e) {
            throw new ClientProtocolException(e);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }


    public static String post(HttpRequestData httpRequestData, String url, Map<String, String> inputs, RequestConfig requestConfig, Map<String, String> headers, List<Header> responseHeaders) throws IOException {
        String responseContent = null;
        try {

            HttpResponse response = postResponse(httpRequestData, url, inputs, requestConfig, headers);

            // 保存响应头
            responseHeaders.clear();
            Header[] returnHeaders = response.getAllHeaders();
            responseHeaders.addAll(Arrays.asList(returnHeaders));

            responseContent = EntityUtils.toString(response.getEntity());
            // 确保实体内容被完全消耗
            EntityUtils.consumeQuietly(response.getEntity());
        } catch (ClientProtocolException e) {
            throw new ClientProtocolException(e);
        } catch (IOException e) {
            throw new IOException(e);
        }
        return responseContent;
    }

    public static String post(HttpRequestData httpRequestData, String url, Map<String, String> inputs, RequestConfig requestConfig, Map<String, String> headers) throws IOException {
        String responseContent = null;
        try {

            HttpResponse response = postResponse(httpRequestData, url, inputs, requestConfig, headers);
            responseContent = EntityUtils.toString(response.getEntity(), "UTF-8");
            // 确保实体内容被完全消耗
            EntityUtils.consumeQuietly(response.getEntity());
        } catch (ClientProtocolException e) {
            throw new ClientProtocolException(e);
        } catch (IOException e) {
            throw new IOException(e);
        }
        return responseContent;
    }

}
