package com.lin.crawler.common.entity;

import java.util.Date;

/**
 *
 * Created by linjinzhi on 2018-12-14.
 *
 * 用户文章，最终使用的数据.
 *
 */
public class MyUserArticle {

    private long id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章头图
     */
    private String headPic;

    /**
     * 图片列表
     */
    private String picList;

    /**
     * 基础标签id
     */
    private long basicTagId;

    /**
     * 基础标签名称  即 四个一级标签
     */
    private String basicTagName;

    /**
     * 标签id
     */
    private long tagId;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 0 正常 1 草稿 2 删除 3 优评 4 审核未通过  5 审核通过  6  已编辑
     */
    private int status;

    /**
     * 质量值
     */
    private int quality;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 用户id
     */
    private long userId;

    /**
     * 点赞数
     */
    private int likeCount;

    /**
     * 评论数
     */
    private int commentCount;

    /**
     * 扩展字段
     */
    private String feature;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getPicList() {
        return picList;
    }

    public void setPicList(String picList) {
        this.picList = picList;
    }

    public long getBasicTagId() {
        return basicTagId;
    }

    public void setBasicTagId(long basicTagId) {
        this.basicTagId = basicTagId;
    }

    public String getBasicTagName() {
        return basicTagName;
    }

    public void setBasicTagName(String basicTagName) {
        this.basicTagName = basicTagName;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }
}
