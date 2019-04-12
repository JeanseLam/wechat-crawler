package com.lin.crawler.common.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SimpleProxyIp {

    private String ip;

    private int port;

    private boolean isValid;

    private boolean isHttps;

    private boolean busy;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isHttps() {
        return isHttps;
    }

    public void setHttps(boolean https) {
        isHttps = https;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    @Override
    public String toString() {
        return "SimpleProxyIp{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", isValid=" + isValid +
                ", isHttps=" + isHttps +
                ", busy=" + busy +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SimpleProxyIp that = (SimpleProxyIp) o;

        return new EqualsBuilder()
                .append(port, that.port)
                .append(isValid, that.isValid)
                .append(isHttps, that.isHttps)
                .append(busy, that.busy)
                .append(ip, that.ip)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(ip)
                .append(port)
                .append(isValid)
                .append(isHttps)
                .append(busy)
                .toHashCode();
    }
}
