package com.lin.crawler.dao;

import com.lin.crawler.common.entity.MyUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class UserDao {

    /**
     * 微信公众号默认用户头像
     */
    public static final String DEFAULT_AVATAR_PIC = "https://file.****.com/download/2018/1226/1545786210592_9013.png";

    @Autowired
    @Qualifier("caimiJdbcTemplate")
    private JdbcTemplate caimiJdbcTemplate;

    public long insert(MyUser myUser) {
        if(myUser != null) {
            String sql = "insert into `caimi_user`(`objectid`, `created_date`, `last_update_date`, `deleted`" +
                    ", `area`, `avatar`, `birthday`, `city`, `email`, `gender`, `intro`, `nick_name`, `openid`," +
                    "`phone_no`, `province`, `status`, `user_no`, `created_by`, `last_updated_by`, `user_id`," +
                    "`n_account_id`, `country`, `unionid`) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            caimiJdbcTemplate.update(
                    new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                            ps.setString(1, myUser.getObjectId());
                            ps.setDate(2, new Date(myUser.getCreatedDate().getTime()));
                            ps.setDate(3, new Date(myUser.getLastUpdateDate().getTime()));
                            ps.setInt(4, myUser.getDeleted());
                            ps.setString(5, myUser.getArea());
                            ps.setString(6, myUser.getAvatar());
                            ps.setDate(7, null);
                            ps.setString(8, myUser.getCity());
                            ps.setString(9, myUser.getEmail());
                            ps.setInt(10, myUser.getGender());
                            ps.setString(11, myUser.getIntro());
                            ps.setString(12, myUser.getNickName());
                            ps.setString(13, myUser.getOpenId());
                            ps.setString(14, myUser.getPhoneNo());
                            ps.setString(15, myUser.getProvince());
                            ps.setInt(16, myUser.getStatus());
                            ps.setLong(17, myUser.getUserNo());
                            ps.setString(18, myUser.getCreatedBy());
                            ps.setString(19, myUser.getLastUpdateBy());
                            ps.setString(20, myUser.getUserId());
                            ps.setString(21, myUser.getnAccountId());
                            ps.setString(22, myUser.getCountry());
                            ps.setString(23, myUser.getUnionId());
                            return ps;
                        }
                    },
                    keyHolder);
            return keyHolder.getKey().longValue();
        }
        return -1L;
    }


    public MyUser queryUserByNickName(String nickName) {
        if(StringUtils.isBlank(nickName)) {
            return null;
        }
        try {
            String sql = "select * from `caimi_user` where `nick_name` = ? limit 1";
            return caimiJdbcTemplate.queryForObject(sql, new RowMapper<MyUser>() {
                @Override
                public MyUser mapRow(ResultSet resultSet, int i) throws SQLException {
                    MyUser myUser = new MyUser();
                    myUser.setId(resultSet.getLong("id"));
                    return myUser;
                }
            }, nickName);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
