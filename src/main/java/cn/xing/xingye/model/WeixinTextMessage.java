package cn.xing.xingye.model;

/**
 * Created by indexing on 16/4/15.
 */
public class WeixinTextMessage extends WeixinMessage {
    private String content;

    public WeixinTextMessage() {
        setMsgType("text");
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
