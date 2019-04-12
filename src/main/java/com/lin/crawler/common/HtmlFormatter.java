package com.lin.crawler.common;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Created by linjinzhi on 2018-12-19.
 *
 * html文本统一格式化工具.
 *
 */
public class HtmlFormatter {

    private static final Logger logger = LoggerFactory.getLogger(HtmlFormatter.class);

    // 正文内容样式
    private static final String contentStyle = "font-size: 16px; line-height: 26px; padding: 4px 4px 4px 4px; text-justify:inter-ideograph; color: rgb(102,102,102)";

    // 文末声明样式和文本
    private static final String footerStyle = "font-weight: bold; font-size: 14px; color: rgba(102, 102, 102, 0.64)";
    private static final String footerText = "如果您有优质的、符合采蜜调性的原创文章，欢迎以个人的名义投稿入驻采蜜名家专栏。投稿方式：请将个人简介以及代表作品发送至caoyanfang@youxin.com，" +
            "并附上电话和微信以便做进一步沟通，在主题中标明：申请入驻采蜜专栏＋投稿人名字";

    // 正文末来源信息样式和文本
    private static final String contentFooterStyle = "color: rgba(102, 102, 102, 0.64); font-size: 12px; font-weight: bold";
    private static final String contentFooterText = "本文内容来源自\"微信公众号\"，不代表采蜜立场，如若转载请联系原作者。";

    // 分割线
    private static final String hr = "<hr style=\"color: rgba(102, 102, 102, 0.64)\"/>";

    private HtmlFormatter() {}

    public static String format(String originHtml) {

        try {

            Whitelist wl = Whitelist.basicWithImages();
            wl.addTags("div", "span", "p", "section", "a", "font");
            String clean = Jsoup.clean(originHtml, wl);

//            String footerHtml = "<div style=\"" + contentFooterStyle + "\">" + contentFooterText + "</div>"+
//                    hr +
//                    "<div style=\"" + footerStyle + "\">" + footerText + "</div>";
            Document document = Jsoup.parse("<div style=\"" + contentStyle + "\">" + clean + "</div>");
            return document.select("div").get(0).toString();
        } catch (Exception e) {
            logger.error("clean and format html style occurs an error");
            logger.error(e.getMessage(), e);
        }
        return originHtml;
    }
}
