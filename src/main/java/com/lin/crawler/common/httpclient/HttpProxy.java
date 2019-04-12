package com.lin.crawler.common.httpclient;

public class HttpProxy {

    private String host;
    private int port;

    public String getHost() {
        return host;
    }

    void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    void setPort(int port) {
        this.port = port;
    }
}