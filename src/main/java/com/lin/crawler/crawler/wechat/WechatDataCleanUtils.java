package com.lin.crawler.crawler.wechat;

import com.lin.crawler.common.MatrixUtil;
import com.lin.crawler.common.httpclient.RequestUtils;
import com.lin.crawler.common.httpclient.HttpRequestData;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

public class WechatDataCleanUtils {

    public static void wechatDataClean(String topic, Document document) {

        // 剔除包含二维码的图片
        try {
            HttpRequestData httpRequestData = new HttpRequestData();
            Elements elements = document.getElementsByTag("img");
            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                String src = element.attr("data-src");

                if (StringUtils.isBlank(src)) {
                    src = element.attr("src");
                }

                if (StringUtils.isNotBlank(src) && (src.startsWith("http") || src.startsWith("https"))) {
                    byte[] image = RequestUtils.getBytes(httpRequestData, src, null, new HashMap<>(0));
                    InputStream inputStream = new ByteArrayInputStream(image);
                    String qrcode = MatrixUtil.decode(inputStream);
                    if (StringUtils.isNotBlank(qrcode)) {
                        element.remove();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 删除最后一张图片和文字
        Elements elements = document.getElementsByTag("img");
        if(elements != null && !elements.isEmpty()) {
            elements.last().remove();
        }

        elements = document.getElementsByTag("p");
        for(int i = elements.size() - 1; i >= 0; i--) {
            Element element = elements.get(i);
            if(element.hasText()) {
                element.remove();
                break;
            }
        }

        // 剔除空行
        try {
            elements = document.getElementsByTag("p");
            for(Element element : elements) {
                if(!element.hasText() && element.select("img").size() == 0) {
                    element.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 只取p标签
     * @param html 正文内容
     * @return String
     */
    public static String onlyP(String html) {

        try {
            Document document = Jsoup.parse(html);
            Elements elements = document.getElementsByTag("p");
            StringBuilder stringBuilder = new StringBuilder();
            for(Element element : elements) {
                stringBuilder.append(element.toString() + "\n");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return html;
    }
}
