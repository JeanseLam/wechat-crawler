package com.lin.crawler.dao;

import com.lin.crawler.common.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TagDao {

    @Autowired
    @Qualifier("ladybugJdbcTemplate")
    private JdbcTemplate ladybugJdbcTemplate;


    /**
     * 查询所有“关键词”下的全部标签信息, 爬虫抓取的文章归类为“关键字”
     * @return List
     */
    public Map<String, Tag> queryTags() {
        List<Tag> tags = ladybugJdbcTemplate.query("SELECT * from ladybug_tag where is_min_level = ? and origin_id = ?", new Object[]{1, 12}, new RowMapper<Tag>() {
            @Override
            public Tag mapRow(ResultSet resultSet, int i) throws SQLException {
                Tag tag = new Tag();
                tag.setId(resultSet.getLong("id"));
                tag.setGmtCreate(resultSet.getDate("gmt_create"));
                tag.setGmtModified(resultSet.getDate("gmt_modified"));
                tag.setTagName(resultSet.getString("tag_name"));
                tag.setTagLevel(resultSet.getInt("tag_level"));
                tag.setParentId(resultSet.getLong("parent_id"));
                tag.setIsUsed(resultSet.getInt("is_used"));
                tag.setStatus(resultSet.getInt("status"));
                tag.setOriginId(resultSet.getLong("origin_id"));
                tag.setIsMinLevel(resultSet.getInt("is_min_level"));
                tag.setMaxLevel(resultSet.getInt("max_level"));
                return tag;
            }
        });
        Map<String, Tag> tagsMap = new HashMap<>();
        for(Tag tag : tags) {
            String tagName = tag.getTagName();
            tagsMap.put(tagName, tag);
        }
        return tagsMap;
    }
}
