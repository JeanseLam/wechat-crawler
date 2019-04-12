package com.lin.crawler;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

public class HtmlFormatterText {

    @Test
    public void test() throws Exception {

        // 正文内容样式
        String contentStyle = "font-size: 16px; line-height: 2; padding: 4px 4px 4px 4px; text-justify:inter-ideograph; color: rgb(102,102,102)";

//        // 文末声明样式和文本
//        String footerStyle = "font-weight: bold; font-size: 14px; color: rgba(102, 102, 102, 0.64)";
//        String footerText = "如果您有优质的、符合采蜜调性的原创文章，欢迎以个人的名义投稿入驻采蜜名家专栏。投稿方式：请将个人简介以及代表作品发送至caoyanfang@youxin.com，" +
//                "并附上电话和微信以便做进一步沟通，在主题中标明：申请入驻采蜜专栏＋投稿人名字";
//
//        // 正文末来源信息样式和文本
//        String contentFooterStyle = "color: rgba(102, 102, 102, 0.64); font-size: 12px; font-weight: bold";
//        String contentFooterText = "本文内容来源自\"微信公众号\"，不代表采蜜立场，如若转载请联系原作者。";
//
//        // 分割线
//        String hr = "<hr style=\"color: rgba(102, 102, 102, 0.64)\"/>";

        String html = IOUtils.toString(new FileReader(new File("C:\\Users\\admin\\Desktop\\test.html")));

        Whitelist wl = Whitelist.basicWithImages();
        wl.addTags("div", "span", "p", "section", "a", "font");
        String clean = Jsoup.clean(html, wl);

        StringBuilder finalHtml = new StringBuilder();
//        finalHtml.append("<div style=\"" + contentFooterStyle + "\">" + contentFooterText + "</div>");
        finalHtml.append("<div style=\"" + contentStyle + "\">" + clean + "</div>");
//        finalHtml.append("<div style=\"" + footerStyle + "\">" + footerText + "</div>");
//        finalHtml.append(hr);
        Document document = Jsoup.parse( finalHtml.toString());

        System.out.println(document.toString());
    }


    @Test
    public void test2() throws Exception {

        String html = IOUtils.toString(new FileReader("C:\\Users\\admin\\Desktop\\html.txt"));
        Document document = Jsoup.parse(html);
        Elements elements = document.select("p");
        Element element = elements.get(elements.size() - 1);
        element.attr("style", "font-weight: bold; font-size: 14px; color: rgba(102, 102, 102, 0.64)");
        String p2 = element.toString();
        element.remove();
        element = elements.get(elements.size() - 2);
        element.attr("style", "color: rgba(102, 102, 102, 0.64); font-size: 12px; font-weight: bold");
        String p1 = element.toString();
        element.remove();

        elements = document.select("hr");
        element = elements.get(elements.size() - 1);
        element.remove();

        html = document.select("body").toString();
        html = html.replaceAll("<body>", "");
        html = html.replaceAll("</body>", "");
        String detail = "<div style=\"font-size: 16px; line-height: 2; padding: 4px 4px 4px 4px; text-justify:inter-ideograph; color: rgb(102,102,102)\">" + html + "</div>";
        html = detail + p1 + "<hr style=\"color: rgba(102, 102, 102, 0.64)\">" + p2;
        System.out.println(html);
    }


    @Test
    public void testSection() throws Exception {
        String html = IOUtils.toString(new FileInputStream("C:\\Users\\admin\\Desktop\\test.html"));
        html = html.replaceAll("<section>", "");
        html = html.replaceAll("</section>", "");
        html = html.replaceAll("\n", "");
        System.out.println(html);
    }
}
