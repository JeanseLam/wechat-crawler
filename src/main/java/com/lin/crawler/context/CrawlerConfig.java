package com.lin.crawler.context;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class CrawlerConfig {

    public String source;

    public String topic;

    public int sourceType;

    public String processClass;

    public String homePageRequest;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CrawlerConfig that = (CrawlerConfig) o;

        return new EqualsBuilder()
                .append(sourceType, that.sourceType)
                .append(source, that.source)
                .append(topic, that.topic)
                .append(processClass, that.processClass)
                .append(homePageRequest, that.homePageRequest)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(source)
                .append(topic)
                .append(sourceType)
                .append(processClass)
                .append(homePageRequest)
                .toHashCode();
    }
}
