package com.linkwechat.action.entity;

import java.io.Serializable;
import java.util.Date;

import com.linkwechat.action.annotation.Column;
import com.linkwechat.action.annotation.Id;
import com.linkwechat.action.annotation.NotDBColumn;
import com.linkwechat.action.annotation.Table;

/**
 * 用户实体
 * 
 * @author linkwechat linkwechat@foxmail.com
 * @version 1.0
 */
@Table(name = "t_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户编号
     */
    @Id
    private Long id;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 用户手机
     */
    @Column(name = "phone")
    private String mobile;

    /**
     * 用户邮件
     */
    private String email;

    /**
     * 时间戳
     */
    @NotDBColumn
    private Date timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", mobile=" + mobile + ", email=" + email + ", timestamp="
                + timestamp + "]";
    }
}
