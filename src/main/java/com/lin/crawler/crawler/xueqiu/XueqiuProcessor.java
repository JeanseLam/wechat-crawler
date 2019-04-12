package com.lin.crawler.crawler.xueqiu;

import com.lin.crawler.common.entity.NewsArticle;
import com.lin.crawler.context.AbstractProcessor;

import java.util.List;

public class XueqiuProcessor extends AbstractProcessor {

    @Override
    public List<NewsArticle> processListPage(Object pageContent) {
        return null;
    }

    @Override
    public void processDetailPage(Object pageContent, NewsArticle newsArticle) {

    }
}
