package cn.xing.xingye.model;

/**
 * Created by indexing on 16/4/15.
 */
public class WeixinImageMessage extends WeixinMediaMessage {
    private String picUrl;

    public WeixinImageMessage() {
        setMsgType("image");
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

}
