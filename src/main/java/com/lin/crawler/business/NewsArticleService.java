package com.lin.crawler.business;

import com.lin.crawler.common.TagMatch;
import com.lin.crawler.common.entity.MyUser;
import com.lin.crawler.common.entity.MyUserArticle;
import com.lin.crawler.common.entity.NewsArticle;
import com.lin.crawler.common.entity.Tag;
import com.lin.crawler.dao.ButterflyDao;
import com.lin.crawler.dao.NewsArticleDao;
import com.lin.crawler.dao.TagDao;
import com.lin.crawler.dao.UserDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

/**
 *
 * Created by linjinzhi on 2018-12-12.
 *
 * 业务相关主键
 *
 */
@Component
public class NewsArticleService {

    private static final Logger logger = LoggerFactory.getLogger(NewsArticleService.class);

    /**
     * 抓取的文章入库完成的id队列
     */
    private Queue<NewsArticle> finishInsertIdQueue = new LinkedList<>();

    @Resource
    private NewsArticleDao newsArticleDao;

    @Resource
    private TagDao tagDao;

    @Resource
    private ButterflyDao butterflyDao;

    @Resource
    private UserDao userDao;

    @PostConstruct
    public void initProcessTagTask() {

        ProcessTagTask processTagTask= new ProcessTagTask();
        new Thread(processTagTask).start();
    }

    public Set<Long> add(List<NewsArticle> articles) {
        return newsArticleDao.insert(articles);
    }

    public boolean isExist(String title) {
        return newsArticleDao.isExist(title);
    }


    /**
     * 抓取解析完成的文章加入缓存队列
     * @param crawlArticles 爬虫抓取的文章
     */
    public void convertCrawlArticleToUserArticle(List<NewsArticle> crawlArticles) {
        for(NewsArticle article : crawlArticles) {
            finishInsertIdQueue.offer(article);
        }
    }



    /**
     * 将爬虫抓取文章转换成最终的用户文章，转换过程需要和标签配对，并同步到数据库.
     * @param crawlArticles 爬虫抓取文章
     * @param userArticles 用户文章
     */
    public void convertCrawlArticleToUserArticle(List<NewsArticle> crawlArticles, List<MyUserArticle> userArticles) {

        Map<String, Tag> tagsMap = tagDao.queryTags();
        // 初始化标签
        TagMatch.initTag(tagsMap.keySet());

        // 根据标题和已有标签配对文章的标签
        for(NewsArticle crawlArticle : crawlArticles) {
            try {
                if(StringUtils.isBlank(crawlArticle.getTitle())) {
                    continue;
                }
                String tagWord = TagMatch.getTagWord(crawlArticle.getContent());
                Tag matchedTag = tagsMap.get(tagWord);
                if(matchedTag != null) {
                    MyUserArticle userArticle = new MyUserArticle();
                    userArticle.setBasicTagId(12L);
                    userArticle.setBasicTagName("关键词");
                    userArticle.setTagId(matchedTag.getId());
                    userArticle.setTagName(matchedTag.getTagName());
                    userArticle.setTitle(crawlArticle.getTitle());
                    userArticle.setCommentCount(0);
                    userArticle.setLikeCount(0);
                    userArticle.setContent(crawlArticle.getContent());
                    userArticle.setGmtCreate(new Date());
                    userArticle.setGmtModified(new Date());
                    userArticle.setHeadPic(crawlArticle.getImageLink());
                    userArticle.setQuality(0);
                    // 默认使用"未审核”状态
                    userArticle.setStatus(-1);
                    userArticle.setPublishTime(new Date());

                    // 设置文章对应的用户id
                    articleAndUserRelate(crawlArticle.getTopic(), userArticle);

                    userArticles.add(userArticle);
                }
            } catch (Exception e) {
                logger.error("convert crawl article to user article fail, title:{}", crawlArticle.getTitle());
                logger.error(e.getMessage(), e);
            }
        }

        // 保存最终的用户文章
        if(!userArticles.isEmpty()) {
            try {
                 butterflyDao.batchInsert(userArticles);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    /**
     * 关联文章和用户信息.
     * @param topic 网站名称或分类
     * @param userArticle 用户文章
     */
    private void articleAndUserRelate(String topic, MyUserArticle userArticle) {

        MyUser myUser = userDao.queryUserByNickName(topic);
        if(myUser != null) {
            userArticle.setUserId(myUser.getId());
            return;
        }

        // 如果不存在就新加用户再关联
        myUser = new MyUser();
        myUser.setCreatedDate(new Date());
        myUser.setDeleted(0);
        myUser.setIntro("爬虫");
        myUser.setGender(1);
        myUser.setLastUpdateDate(new Date());
        myUser.setNickName(topic);
        myUser.setObjectId(UUID.randomUUID().toString());
        long userId = userDao.insert(myUser);
        if(userId < 0) {
            logger.error("insert new my user fail:{}", myUser.toString());
        } else {
            userArticle.setUserId(userId);
        }
    }


    private class ProcessTagTask implements Runnable {

        final int batchProcessSize = 100;

        @Override
        public void run() {

            logger.info("process tag task start...");

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    List<NewsArticle> articles = new ArrayList<>();
                    while (true) {

                        if(articles.size() >= batchProcessSize) {
                            break;
                        }
                        NewsArticle newsArticle = finishInsertIdQueue.poll();
                        if(newsArticle != null) {
                            articles.add(newsArticle);
                        } else {
                            break;
                        }
                        Thread.sleep(50L);
                    }

                    if(!articles.isEmpty()) {
                        List<MyUserArticle> userArticles = new ArrayList<>();
                        convertCrawlArticleToUserArticle(articles, userArticles);
                        logger.info("convert crawl article to user article size:{}", userArticles.size());

                        articles.clear();
                    }

                    Thread.sleep(50L);

                } catch (Exception e) {
                    logger.error("process tag task occurs error, message:{}, exception:{}", e.getMessage(), e);
                }
            }
        }
    }
}
