package com.lin.crawler.dao;

import com.lin.crawler.common.entity.MyUserArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class ButterflyDao {

    @Autowired
    @Qualifier("butterflyJdbcTemplate")
    private JdbcTemplate butterflyJdbcTemplate;


    public List<MyUserArticle> getArticleByMinId(long minId) {

        return butterflyJdbcTemplate.query("select * from butterfly_user_article where user_id = 13 and id >= " + minId, new RowMapper<MyUserArticle>() {
            @Override
            public MyUserArticle mapRow(ResultSet resultSet, int i) throws SQLException {
                MyUserArticle myUserArticle = new MyUserArticle();
                myUserArticle.setId(resultSet.getLong("id"));
                myUserArticle.setContent(resultSet.getString("content"));
                return myUserArticle;
            }
        });
    }

    public void batchUpdate(List<MyUserArticle> articles) {

        String sql = "update butterfly_user_article set content = ?, gmt_modified = now()  where id = ?";
        final int batchSize = 100;
        for(int i = 0; i < articles.size(); i += batchSize) {
            final List<MyUserArticle> batchList = articles.subList(i, i + batchSize > articles.size() ? articles.size() : i + batchSize);
            butterflyJdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int j) throws SQLException {
                    MyUserArticle article = batchList.get(j);
                    preparedStatement.setString(1, article.getContent());
                    preparedStatement.setLong(2, article.getId());
                }

                @Override
                public int getBatchSize() {
                    return batchList.size();
                }
            });
        }
    }

    public void batchInsert(List<MyUserArticle> articles) {

        String insertSQL = "insert into `butterfly_user_article`(" +
                "`gmt_create`, `gmt_modified`, `title`, `content`, `head_pic`, `pic_list`, " +
                "`basic_tag_id`, `basic_tag_name`, `tag_id`, `tag_name`, `status`, `quality`," +
                "`publish_time`, `user_id`, `like_count`, `comment_count`, `feature`, `edited`)values(" +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        final int batchSize = 100;
        for(int i = 0; i < articles.size(); i += batchSize) {
            final List<MyUserArticle> batchList = articles.subList(i, i + batchSize > articles.size() ? articles.size() : i + batchSize);
            butterflyJdbcTemplate.batchUpdate(insertSQL, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int j) throws SQLException {
                    MyUserArticle article = batchList.get(j);
                    preparedStatement.setDate(1, new Date(article.getGmtCreate().getTime()));
                    preparedStatement.setDate(2, new Date(article.getGmtModified().getTime()));
                    preparedStatement.setString(3, article.getTitle());

//                    try {
//                        preparedStatement.setString(4, new String(article.getContent().getBytes("UTF-8"),"UTF-8"));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    preparedStatement.setString(4, article.getContent());

                    preparedStatement.setString(5, article.getHeadPic());
                    preparedStatement.setString(6, article.getPicList());
                    preparedStatement.setLong(7, article.getBasicTagId());
                    preparedStatement.setString(8, article.getBasicTagName());
                    preparedStatement.setLong(9, article.getTagId());
                    preparedStatement.setString(10, article.getTagName());
                    preparedStatement.setInt(11, article.getStatus());
                    preparedStatement.setInt(12, article.getQuality());
                    preparedStatement.setDate(13, article.getPublishTime() == null ? null : new Date(article.getPublishTime().getTime()));
                    preparedStatement.setLong(14, article.getUserId());
                    preparedStatement.setInt(15, article.getLikeCount());
                    preparedStatement.setInt(16, article.getCommentCount());
                    preparedStatement.setString(17, article.getFeature());
                    // 默认设置成未编辑状态
                    preparedStatement.setInt(18, 0);
                }

                @Override
                public int getBatchSize() {
                    return batchList.size();
                }
            });
        }
    }
}