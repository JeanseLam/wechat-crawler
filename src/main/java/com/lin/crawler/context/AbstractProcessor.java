package com.lin.crawler.context;

import com.lin.crawler.common.ApplicationContextUtils;
import com.lin.crawler.common.HtmlFormatter;
import com.lin.crawler.common.entity.NewsArticle;
import com.lin.crawler.common.file.FileServerHelper;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 * Created by linjinzhi on 2018-12-12.
 *
 * 抽象结果处理器.
 *
 */
public abstract class AbstractProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AbstractProcessor.class);

    private FileServerHelper fileServerHelper = ApplicationContextUtils.getBean(FileServerHelper.class);

    /**
     * 将图片上传到文件服务器并设置成文件服务器的目录, 去掉超链接
     * @param crawlerSession 爬虫会话
     */
    public void processImageLink(LocalCrawlerSession crawlerSession) {

        List<NewsArticle> articles = crawlerSession.getArticles();
        for(NewsArticle newsArticle : articles) {

            try {

                // 处理摘要图片
                String remoteLink = fileServerHelper.upload(newsArticle.getImageLink());
                newsArticle.setImageLink(remoteLink);

                // 处理内容中的图片
                String content = newsArticle.getContent();
                Document document = Jsoup.parse(content);

                // 去掉所有超链接
                Elements elements = document.getElementsByTag("a");
                for (Element element : elements) {
                    element.attr("href", "");
                    element.text("");
                }

                // 图片下载和替换
                elements = document.getElementsByTag("img");
                for(Element element : elements) {

                    String dataSrc = element.attr("data-src");
                    String src = element.attr("src");

                    if(StringUtils.isNotBlank(dataSrc) && StringUtils.isBlank(src)) {

                        String link = fileServerHelper.upload(dataSrc);
                        element.attr("data-src", link);
                        element.attr("src", link);

                    } else if(StringUtils.isBlank(dataSrc) && StringUtils.isNotBlank(src)) {
                        String link = fileServerHelper.upload(src);
                        element.attr("src", link);

                    } else if(StringUtils.isNotBlank(dataSrc) && StringUtils.isNotBlank(src)) {
                        String dataSrcLink = fileServerHelper.upload(dataSrc);
                        String srcLink = fileServerHelper.upload(src);
                        element.attr("data-src", dataSrcLink);
                        element.attr("src", srcLink);

                    } else {
                        // 图片不是通过data-src和src属性来设置，记录下来
                        logger.error("image is not set into attribute data-src or src");
                        logger.error(element.html());
                    }
                }

                // 替换原来的文本
                newsArticle.setContent(document.toString());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    /**
     * 处理文章内容的html格式
     * @param newsArticles 文章数据列表
     */
    public void processHtmlContentFormat(List<NewsArticle> newsArticles) {

        for(NewsArticle article : newsArticles) {
            String content = article.getContent();
            content = HtmlFormatter.format(content);
            article.setContent(content);
        }
    }


    /**
     * 解析文章列表数据
     * @param pageContent 列表页面
     * @return 文章列表
     */
    public abstract List<NewsArticle> processListPage(Object pageContent);


    /**
     * 解析文章详情页面数据
     * @param pageContent 详情页面
     * @param newsArticle 文章数据封装
     * @return NewsArticle
     */
    public abstract void processDetailPage(Object pageContent, NewsArticle newsArticle);

}
