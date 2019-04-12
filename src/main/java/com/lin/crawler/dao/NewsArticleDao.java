package com.lin.crawler.dao;

import com.lin.crawler.common.entity.NewsArticle;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class NewsArticleDao {

    private static final Logger logger = LoggerFactory.getLogger(NewsArticleDao.class);

    @Autowired
    @Qualifier("butterflyJdbcTemplate")
    private JdbcTemplate butterflyJdbcTemplate;

    /**
     * 数据入库，采集的原始数据，数据保存在butterfly/news_article
     *
     * @param newsArticles 文章数据
     */
    public Set<Long> insert(List<NewsArticle> newsArticles) {

        Set<Long> idSet = new HashSet<>();

        if (newsArticles != null && !newsArticles.isEmpty()) {

            for (NewsArticle newsArticle : newsArticles) {

                String insertSQL = "insert into `butterfly_crawl_article`" +
                        "(`title`, `abstract_text`, `author`, `image_link`, `detail_link`, `content`, `source`, `topic`, `source_type`, `crawl_time`, `update_time`) values (" +
                        "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try {
                    if (isExist(newsArticle.getTitle())) {
                        continue;
                    }

                    KeyHolder keyHolder = new GeneratedKeyHolder();

                    butterflyJdbcTemplate.update(
                            new PreparedStatementCreator() {
                                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                                    PreparedStatement ps = connection.prepareStatement(insertSQL, new String[]{"id"});
                                    ps.setString(1, newsArticle.getTitle());
                                    ps.setString(2, newsArticle.getAbstractText());
                                    ps.setString(3, newsArticle.getAuthor());
                                    ps.setString(4, newsArticle.getImageLink());
                                    ps.setString(5, newsArticle.getDetailLink());

                                    try {
                                        ps.setString(6, new String(newsArticle.getContent().getBytes("UTF-8"), "UTF-8"));
                                    } catch (Exception e) {
                                        logger.error(e.getMessage(), e);
                                    }
                                    ps.setString(7, newsArticle.getSource());
                                    ps.setString(8, newsArticle.getTopic());
                                    ps.setInt(9, newsArticle.getSourceType());
                                    ps.setDate(10, new Date(newsArticle.getCrawlTime().getTime()));
                                    ps.setDate(11, new Date(System.currentTimeMillis()));
                                    return ps;
                                }
                            },
                            keyHolder);

                    idSet.add(keyHolder.getKey().longValue());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return idSet;
    }


    public boolean isExist(String title) {
        try {
            String existsTitle = butterflyJdbcTemplate.queryForObject("select title from butterfly_crawl_article where title = '" + title + "'", String.class);
            return StringUtils.isNotBlank(existsTitle);
        } catch (Exception e) {
            return false;
        }
    }


    public List<NewsArticle> queryArticlesByMinId(long minId) {
        return butterflyJdbcTemplate.query("select * from butterfly_crawl_article where id >= ?", new Object[]{minId}, new RowMapper<NewsArticle>() {
            @Override
            public NewsArticle mapRow(ResultSet resultSet, int i) throws SQLException {
                NewsArticle newsArticle = new NewsArticle();
                newsArticle.setId(resultSet.getLong("id"));
                newsArticle.setTitle(resultSet.getString("title"));
                newsArticle.setAbstractText(resultSet.getString("abstract_text"));
                newsArticle.setAuthor(resultSet.getString("author"));
                newsArticle.setImageLink(resultSet.getString("image_link"));
                newsArticle.setDetailLink(resultSet.getString("detail_link"));
                newsArticle.setContent(resultSet.getString("content"));
                newsArticle.setSourceType(resultSet.getInt("source_type"));
                newsArticle.setSource(resultSet.getString("source"));
                newsArticle.setTopic(resultSet.getString("topic"));
                newsArticle.setCrawlTime(resultSet.getDate("crawl_time"));
                newsArticle.setUpdateTime(resultSet.getDate("update_time"));
                return newsArticle;
            }
        });
    }

    public void update(List<NewsArticle> articles) {
        for (NewsArticle newsArticle : articles) {

            String updateSQL = "update `butterfly_crawl_article`" +
                    "set `title` = ?, `abstract_text` = ?, `author` = ?, `image_link`= ?, `detail_link` = ?, `content` = ?, `source` = ?, `topic` = ?," +
                    " `source_type` = ?, `crawl_time` = ?, `update_time` = ? where id = ?";

            try {

                butterflyJdbcTemplate.update(
                        new PreparedStatementCreator() {
                            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                                PreparedStatement ps = connection.prepareStatement(updateSQL);
                                ps.setString(1, newsArticle.getTitle());
                                ps.setString(2, newsArticle.getAbstractText());
                                ps.setString(3, newsArticle.getAuthor());
                                ps.setString(4, newsArticle.getImageLink());
                                ps.setString(5, newsArticle.getDetailLink());

                                try {
                                    ps.setString(6, new String(newsArticle.getContent().getBytes("UTF-8"), "UTF-8"));
                                } catch (Exception e) {
                                    logger.error(e.getMessage(), e);
                                }
                                ps.setString(7, newsArticle.getSource());
                                ps.setString(8, newsArticle.getTopic());
                                ps.setInt(9, newsArticle.getSourceType());
                                ps.setDate(10, new Date(newsArticle.getCrawlTime().getTime()));
                                ps.setDate(11, new Date(System.currentTimeMillis()));
                                ps.setLong(12, newsArticle.getId());
                                return ps;
                            }
                        });
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
