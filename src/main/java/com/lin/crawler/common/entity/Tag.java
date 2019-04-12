package com.lin.crawler.common.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

public class Tag {

    private long id;

    private Date gmtCreate;

    private Date gmtModified;

    private String tagName;

    private int tagLevel;

    private long parentId;

    private int isUsed;

    private int status;

    private long originId;

    private int isMinLevel;

    private int maxLevel;

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

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getTagLevel() {
        return tagLevel;
    }

    public void setTagLevel(int tagLevel) {
        this.tagLevel = tagLevel;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public int getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(int isUsed) {
        this.isUsed = isUsed;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getOriginId() {
        return originId;
    }

    public void setOriginId(long originId) {
        this.originId = originId;
    }

    public int getIsMinLevel() {
        return isMinLevel;
    }

    public void setIsMinLevel(int isMinLevel) {
        this.isMinLevel = isMinLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", tagName='" + tagName + '\'' +
                ", tagLevel=" + tagLevel +
                ", parentId=" + parentId +
                ", isUsed=" + isUsed +
                ", status=" + status +
                ", originId=" + originId +
                ", isMinLevel=" + isMinLevel +
                ", maxLevel=" + maxLevel +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        return new EqualsBuilder()
                .append(id, tag.id)
                .append(tagLevel, tag.tagLevel)
                .append(parentId, tag.parentId)
                .append(isUsed, tag.isUsed)
                .append(status, tag.status)
                .append(originId, tag.originId)
                .append(isMinLevel, tag.isMinLevel)
                .append(maxLevel, tag.maxLevel)
                .append(gmtCreate, tag.gmtCreate)
                .append(gmtModified, tag.gmtModified)
                .append(tagName, tag.tagName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(gmtCreate)
                .append(gmtModified)
                .append(tagName)
                .append(tagLevel)
                .append(parentId)
                .append(isUsed)
                .append(status)
                .append(originId)
                .append(isMinLevel)
                .append(maxLevel)
                .toHashCode();
    }
}
