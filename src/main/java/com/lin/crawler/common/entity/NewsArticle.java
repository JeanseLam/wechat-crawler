package com.lin.crawler.common.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

/**
 *
 * Created by linjinzhi on 2018-12-12.
 *
 * 新闻文章信息.
 *
 */
public class NewsArticle {

    /**
     * 主键ID
     */
    private long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 摘要图片链接
     */
    private String imageLink;

    /**
     * 抓取来源
     */
    private String source;

    /**
     * 二级来源，用于区分微信公众号
     */
    private String topic;

    /**
     * 文章作者
     */
    private String author;

    /**
     * 摘要文本
     */
    private String abstractText;

    /**
     * 内容主题
     */
    private String content;

    /**
     * 文章详情请求链接
     */
    private String detailLink;

    /**
     * 抓取时间
     */
    private Date crawlTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 抓取来源标识，0：微信公众号，1：网站
     */
    private int sourceType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDetailLink() {
        return detailLink;
    }

    public void setDetailLink(String detailLink) {
        this.detailLink = detailLink;
    }

    public Date getCrawlTime() {
        return crawlTime;
    }

    public void setCrawlTime(Date crawlTime) {
        this.crawlTime = crawlTime;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "NewsArticle{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", source='" + source + '\'' +
                ", topic='" + topic + '\'' +
                ", author='" + author + '\'' +
                ", abstractText='" + abstractText + '\'' +
                ", content='" + content + '\'' +
                ", detailLink='" + detailLink + '\'' +
                ", crawlTime=" + crawlTime +
                ", updateTime=" + updateTime +
                ", sourceType=" + sourceType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NewsArticle that = (NewsArticle) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(sourceType, that.sourceType)
                .append(title, that.title)
                .append(imageLink, that.imageLink)
                .append(source, that.source)
                .append(topic, that.topic)
                .append(author, that.author)
                .append(abstractText, that.abstractText)
                .append(content, that.content)
                .append(detailLink, that.detailLink)
                .append(crawlTime, that.crawlTime)
                .append(updateTime, that.updateTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(title)
                .append(imageLink)
                .append(source)
                .append(topic)
                .append(author)
                .append(abstractText)
                .append(content)
                .append(detailLink)
                .append(crawlTime)
                .append(updateTime)
                .append(sourceType)
                .toHashCode();
    }
}
