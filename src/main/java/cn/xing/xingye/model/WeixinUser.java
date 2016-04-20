package cn.xing.xingye.model;

/**
 * Created by indexing on 16/4/19.
 */
public class WeixinUser extends BaseModel {
    private String openId;
    private Long userId;

    public WeixinUser() {
    }

    public WeixinUser(String openId) {
        this.openId = openId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
