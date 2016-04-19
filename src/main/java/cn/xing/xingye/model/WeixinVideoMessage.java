package cn.xing.xingye.model;

/**
 * Created by indexing on 16/4/15.
 */
public class WeixinVideoMessage extends WeixinMediaMessage {
    private String thumbMediaId;

    public String getThumbMediaId() {
        return thumbMediaId;
    }

    public void setThumbMediaId(String thumbMediaId) {
        this.thumbMediaId = thumbMediaId;
    }
}
