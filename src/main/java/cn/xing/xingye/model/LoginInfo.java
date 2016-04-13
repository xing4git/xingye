package cn.xing.xingye.model;

import java.io.Serializable;

/**
 * Created by indexing on 16/4/13.
 */
public class LoginInfo implements Serializable {
    private User user;
    private long loginTime;
    private String token;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
