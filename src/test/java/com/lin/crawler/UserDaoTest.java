package com.lin.crawler;

import com.lin.crawler.common.entity.MyUser;
import com.lin.crawler.dao.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDaoTest {

    @Resource
    private UserDao userDao;

    @Test
    public void test() {
        MyUser myUser = userDao.queryUserByNickName("财经十一人");
        System.out.println(myUser.getId());
    }
}
