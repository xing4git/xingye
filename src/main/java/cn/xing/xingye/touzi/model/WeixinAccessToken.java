package cn.xing.xingye.touzi.model;

/**
 * Created by indexing on 16/4/14.
 */
public class WeixinAccessToken {
    public String accessToken;
    public long expireTime;
    public long created;

    /**
     * 是否已过期
     */
    public boolean isExpired() {
        return expireTime <= 0 || System.currentTimeMillis() > expireTime;
    }
}
