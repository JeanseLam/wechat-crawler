package com.lin.crawler.crawler.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lin.crawler.common.MyDateUtils;
import com.lin.crawler.common.VerifyCodeService;
import com.lin.crawler.common.httpclient.RequestUtils;
import com.lin.crawler.common.httpclient.HttpRequestData;
import com.lin.crawler.context.AbstractCrawler;
import com.lin.crawler.context.Crawler;
import com.lin.crawler.context.LocalCrawlerSession;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by linjinzhi on 2018-12-12.
 *
 * 微信公众号资讯爬虫.
 *
 */
@Component
@Crawler(source = "wechat")
public class WechatCrawler extends AbstractCrawler {

    private static final Logger logger = LoggerFactory.getLogger(WechatCrawler.class);

    @Resource
    private VerifyCodeService verifyCodeService;

    @Override
    public String crawlHomeRequest(LocalCrawlerSession crawlerSession) {

        Map<String, String> headers = new HashMap<>();
        HttpRequestData httpRequestData = crawlerSession.getHttpRequestData();
        try {
            // 从搜狗搜索微信公众号
            headers.put("Host", "weixin.sogou.com");
            headers.put("Connection", "keep-alive");
            headers.put("Upgrade-Insecure-Requests", "1");
            headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
            headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            headers.put("Referer", "https://weixin.sogou.com/");
            headers.put("Accept-Language", "zh-CN,zh;q=0.9");
            String url = "https://weixin.sogou.com/weixin?type=1&query={0}&ie=utf8&s_from=input&_sug_=n&_sug_type_=";
            url = MessageFormat.format(url, crawlerSession.getCrawlerConfig().topic);

            httpRequestData.displayCookies();

            // 搜索结果页
            String html = RequestUtils.get(httpRequestData, url, null, headers);
            String accountAntiUrl = parseAccountAntiUrl(html);
            headers.put("Accept", "*/*");
            headers.put("Referer", url);
            String tmpHtml = RequestUtils.get(httpRequestData, "https://weixin.sogou.com/websearch/wexinurlenc_sogou_profile.jsp", null, headers);

            if(StringUtils.isNotBlank(accountAntiUrl)) {
                headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
                headers.put("X-Requested-With", "XMLHttpRequest");
                tmpHtml = RequestUtils.get(httpRequestData, accountAntiUrl, null, headers);
            }

            httpRequestData.displayCookies();

            // 判断是否需要打码
            html = processSougouVerifyCode(html, httpRequestData, headers,
                    MessageFormat.format(
                            "/weixin?type=1&query={0}&ie=utf8&s_from=input&_sug_=y&_sug_type_=",
                            crawlerSession.getCrawlerConfig().topic),
                    crawlerSession.getCrawlerConfig().topic);

            return getHomePage(html, crawlerSession, headers, url);

        } catch (Exception e) {
            logger.error("crawl wechat home page fail, message:{}, exception:{}", e.getMessage(), e);
            return "";
        }
    }



    private String parseAccountAntiUrl(String html) throws Exception {
        Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByTag("script");
        for(Element element : elements) {
            if(element.select("script").size() == 1 && element.toString().contains("account_anti_url")) {
                String url = element.toString();
                url = url.substring(url.indexOf("\"") + 1, url.lastIndexOf("\""));
                return "https://weixin.sogou.com" + url;
            }
        }
        return "";
    }


    @Override
    public String crawlNextPageRequest(LocalCrawlerSession crawlerSession) {
        // 搜狗微信不存在下一页
        return null;
    }

    @Override
    public String auth(LocalCrawlerSession crawlerSession, Object pageContent, String request) {

        String html = (String) pageContent;
        Map<String, String> headers = new HashMap<>();
        HttpRequestData httpRequestData = crawlerSession.getHttpRequestData();
        String vcInput = "";
        if(html.contains("请输入验证码")) {
            try {
                headers.put("Host", "mp.weixin.qq.com");
                headers.put("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
                headers.put("Proxy-Connection", "keep-alive");

//                String vcRequest = Jsoup.parse(html).select("#verify_img").attr("src");
                String random = Long.toString(System.currentTimeMillis()) + Double.toString(Math.random()).substring(0, 6);
                String vcRequest = "https://mp.weixin.qq.com" + "/mp/verifycode?cert=" + random;

                byte[] vc = RequestUtils.getBytes(httpRequestData, vcRequest, null, headers);
                vcInput = verifyCodeService.vc(vc, 1004, httpRequestData);

                if(StringUtils.isBlank(vcInput)) {
                    logger.error("云打码失败");
                }

            } catch (Exception e) {
                logger.error("crawl weixin verify code fail, message:{}, exception:{}", e.getMessage(), e);
            }

            // 提交验证码
            try {
                headers.put("Origin", "http://mp.weixin.qq.com");
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                headers.put("Accept", "*/*");
                Map<String, String> params = new HashMap<>();
                String random = Long.toString(System.currentTimeMillis()) + Double.toString(Math.random()).substring(0, 6);
                params.put("cert", random);
                params.put("input", vcInput);
                params.put("appmsg_token", "");
                html = RequestUtils.post(httpRequestData, "http://mp.weixin.qq.com/mp/verifycode", params, headers);

                logger.info("提交微信验证码返回:{}", html);

                // 验证码识别成功
                String errMsg = JSON.parseObject(html).getString("errmsg");
                if(StringUtils.isBlank(errMsg)) {
                    headers.put("Upgrade-Insecure-Requests", "1");
                    headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                    headers.put("Accept-Language", "zh-CN,zh;q=0.9");
                    html = RequestUtils.get(httpRequestData, request, null, headers);
                    return html;
                }

            } catch (Exception e) {
                logger.error("post verify code fail, message:{}, exception:{}", e.getMessage(), e);
            }
            return "";
        } else {
            return html;
        }
    }

    private String processSougouVerifyCode(String html, HttpRequestData httpRequestData, Map<String, String> headers, String referer, String wechatTopic) {
        if(StringUtils.isNotBlank(html) && html.contains("您的访问出错了")) {
            String vcInput = "";
            try {
                Document document = Jsoup.parse(html);
                String vcRequest = document.select("#seccodeImage").attr("src");
                vcRequest = "https://weixin.sogou.com/antispider/" + vcRequest;

                headers.put("Host", "weixin.sogou.com");
                headers.put("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
                headers.put("Referer", "https://weixin.sogou.com/antispider/?from=" + referer);

                byte[] vc = RequestUtils.getBytes(httpRequestData, vcRequest, null, headers);
                vcInput = verifyCodeService.vc(vc, 1006, httpRequestData);

                // 记录下验证码图片
                IOUtils.write(vc, new FileOutputStream("C:\\tmp\\vc\\sougou.png"));
//                vcInput = new Scanner(System.in).next();

                if(StringUtils.isBlank(vcInput)) {
                    logger.error("云打码失败");
                }
                html = submitSougouVc(vcInput, referer, httpRequestData);

                // 验证码识别成功
                if(html.contains("解封成功")) {

                    // 从上报验证码成功返回的消息中解析id，将此id作为新的cookies
                    String id = parseSubmitSOugouVcCodeReturnId(html);

                    // 重新请求搜索
                    headers.put("Upgrade-Insecure-Requests", "1");
                    headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                    // 搜索结果页
                    String url = "https://weixin.sogou.com/weixin?type=1&query={0}&ie=utf8&s_from=input&_sug_=n&_sug_type_=";
                    html = RequestUtils.get(httpRequestData, MessageFormat.format(url, wechatTopic), null, headers);
                    String accountAntiUrl = parseAccountAntiUrl(html);
                    headers.put("Accept", "*/*");
                    headers.put("Referer", url);
                    String tmpHtml = RequestUtils.get(httpRequestData, "https://weixin.sogou.com/websearch/wexinurlenc_sogou_profile.jsp", null, headers);

                    headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    tmpHtml = RequestUtils.get(httpRequestData, accountAntiUrl, null, headers);
                    return html;
                }

            } catch (Exception e) {
                logger.error("crawl sougou verify code fail, message:{}, exception:{}", e.getMessage(), e);
                return "";
            }
            return "";
        } else {
            return html;
        }
    }


    /**
     * 提交搜狗验证码请求
     * @param vc 输入的验证码
     * @return 请求返回结果
     */
    private String submitSougouVc(String vc, String reference, HttpRequestData httpRequestData) {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Host", "weixin.sogou.com");
            headers.put("Connection", "keep-alive");
            headers.put("Origin", "https://weixin.sogou.com");
            headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
            headers.put("X-Requested-With", "XMLHttpRequest");
            headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            headers.put("Referer", "https://weixin.sogou.com/antispider/?from=" + reference);
            Map<String, String> params = new HashMap<>();
            params.put("c", vc);
            params.put("r", reference);
            params.put("v", "5");

            // 关键cookie

            String html = RequestUtils.post(httpRequestData, "https://weixin.sogou.com/antispider/thank.php", params, headers);

            logger.info("云打码输入验证码:{}", vc);
            logger.info("提交搜狗验证码返回:{}", html);
            return html;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "";
        }
    }



    private String getHomePage(String html, LocalCrawlerSession crawlerSession, Map<String, String> headers, String queryUrl) throws IOException {
        // 解析第一条结果
        if(StringUtils.isBlank(html)) {
            logger.error("request home page fail");
            return StringUtils.EMPTY;
        }

        HttpRequestData httpRequestData = crawlerSession.getHttpRequestData();

        Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByTag("a");
        for(Element element : elements) {
            if(element.select("a").size() == 1 && element.hasAttr("uigs") && "account_name_0".equals(element.attr("uigs"))) {
                String link = element.attr("href");
                logger.info("home link:{}", link);

                html = RequestUtils.get(httpRequestData, "https://weixin.sogou.com" + link, null, headers);
                html = processSougouVerifyCode(html, httpRequestData, headers, "https://weixin.sogou.com" + link, crawlerSession.getCrawlerConfig().topic);

                document = Jsoup.parse(html);
                elements = document.getElementsByTag("a");
                for(Element a : elements) {
                    if(a.select("a").size() == 1 && a.hasAttr("uigs") && "account_name_0".equals(a.attr("uigs"))) {
                        link = a.attr("href");
                        break;
                    }
                }
                link = "https://weixin.sogou.com" + link;

                // 请求得到微信公众号首页，需要进行字符串拼接得到首页链接
                headers.put("Upgrade-Insecure- Requests", "1");
                headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                headers.put("Referer", queryUrl);
                httpRequestData.displayCookies();
                html = RequestUtils.get(httpRequestData, link + "&k=67&h=W", null, headers);
                link = parseHomeWeixinRequestFromScript(html);
                return link;
            }
        }
        return "";
    }


    private String parseSubmitSOugouVcCodeReturnId(String json) throws Exception {
        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject.getString("id");
    }


    private String parseHomeWeixinRequestFromScript(String html) throws IOException {

        if(StringUtils.isBlank(html)) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        List lines = IOUtils.readLines(new ByteArrayInputStream(html.getBytes("UTF-8")));
        for(Object line : lines) {
            String str = (String) line;
            if(StringUtils.isNotBlank(str) && str.contains("'")) {
                str = str.substring(str.indexOf('\'') + 1, str.lastIndexOf('\''));
                stringBuilder.append(str);
            }
        }
        String url = stringBuilder.toString();
        logger.info("parse weixin home link:{}", url);
        return url;
    }
}
