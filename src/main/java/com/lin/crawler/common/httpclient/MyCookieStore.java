package com.lin.crawler.common.httpclient;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyCookieStore implements CookieStore {
    List<Cookie> cookieList = new ArrayList<>();

    @Override
    public void addCookie(Cookie cookie) {
        cookieList.add(cookie);
    }

    @Override
    public List<Cookie> getCookies() {
        return cookieList;
    }

    @Override
    public boolean clearExpired(Date date) {
        return false;
    }

    @Override
    public void clear() {
        cookieList.clear();
    }
}
