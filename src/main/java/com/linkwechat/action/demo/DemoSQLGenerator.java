package com.linkwechat.action.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.linkwechat.action.core.SQLGenerator;
import com.linkwechat.action.entity.User;

public class DemoSQLGenerator {

    public static void main(String[] args) {
        User user = new User();
        user.setId(10000000L);
        user.setUsername("linkwechat");
        user.setMobile("13888888888");
        user.setEmail("linkwechat@foxmail.com");
        user.setTimestamp(new Date());

        List<User> userList = new ArrayList<User>();
        userList.add(user);

        try {
            System.out.println(SQLGenerator.createInsertSQL(userList).get(0));
            System.out.println(SQLGenerator.createDeleteSQL(userList).get(0));
            System.out.println(SQLGenerator.createSelectSQL(userList).get(0));
            System.out.println(SQLGenerator.createUpdateSQL(userList).get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
