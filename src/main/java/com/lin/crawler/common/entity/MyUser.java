package com.lin.crawler.common.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

public class MyUser {

    private String objectId;

    private long id;

    private Date createdDate;

    private Date lastUpdateDate;

    private int deleted;

    private String area;

    private String avatar;

    private Date birthday;

    private String city;

    private String email;

    private int gender;

    private String intro;

    private String nickName;

    private String openId;

    private String phoneNo;

    private String province;

    private int status;

    private long userNo;

    private String createdBy;

    private String lastUpdateBy;

    private String userId;

    private String nAccountId;

    private String country;

    private String unionId;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getUserNo() {
        return userNo;
    }

    public void setUserNo(long userNo) {
        this.userNo = userNo;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getnAccountId() {
        return nAccountId;
    }

    public void setnAccountId(String nAccountId) {
        this.nAccountId = nAccountId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    @Override
    public String toString() {
        return "MyUser{" +
                "objectId='" + objectId + '\'' +
                ", id=" + id +
                ", createdDate=" + createdDate +
                ", lastUpdateDate=" + lastUpdateDate +
                ", deleted=" + deleted +
                ", area='" + area + '\'' +
                ", avatar='" + avatar + '\'' +
                ", birthday=" + birthday +
                ", city='" + city + '\'' +
                ", email='" + email + '\'' +
                ", gender=" + gender +
                ", intro='" + intro + '\'' +
                ", nickName='" + nickName + '\'' +
                ", openId='" + openId + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", province='" + province + '\'' +
                ", status=" + status +
                ", userNo=" + userNo +
                ", createdBy='" + createdBy + '\'' +
                ", lastUpdateBy='" + lastUpdateBy + '\'' +
                ", userId='" + userId + '\'' +
                ", nAccountId='" + nAccountId + '\'' +
                ", country='" + country + '\'' +
                ", unionId='" + unionId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MyUser myUser = (MyUser) o;

        return new EqualsBuilder()
                .append(id, myUser.id)
                .append(deleted, myUser.deleted)
                .append(gender, myUser.gender)
                .append(status, myUser.status)
                .append(userNo, myUser.userNo)
                .append(objectId, myUser.objectId)
                .append(createdDate, myUser.createdDate)
                .append(lastUpdateDate, myUser.lastUpdateDate)
                .append(area, myUser.area)
                .append(avatar, myUser.avatar)
                .append(birthday, myUser.birthday)
                .append(city, myUser.city)
                .append(email, myUser.email)
                .append(intro, myUser.intro)
                .append(nickName, myUser.nickName)
                .append(openId, myUser.openId)
                .append(phoneNo, myUser.phoneNo)
                .append(province, myUser.province)
                .append(createdBy, myUser.createdBy)
                .append(lastUpdateBy, myUser.lastUpdateBy)
                .append(userId, myUser.userId)
                .append(nAccountId, myUser.nAccountId)
                .append(country, myUser.country)
                .append(unionId, myUser.unionId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(objectId)
                .append(id)
                .append(createdDate)
                .append(lastUpdateDate)
                .append(deleted)
                .append(area)
                .append(avatar)
                .append(birthday)
                .append(city)
                .append(email)
                .append(gender)
                .append(intro)
                .append(nickName)
                .append(openId)
                .append(phoneNo)
                .append(province)
                .append(status)
                .append(userNo)
                .append(createdBy)
                .append(lastUpdateBy)
                .append(userId)
                .append(nAccountId)
                .append(country)
                .append(unionId)
                .toHashCode();
    }
}
