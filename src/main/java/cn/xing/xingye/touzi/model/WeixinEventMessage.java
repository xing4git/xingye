package cn.xing.xingye.touzi.model;

/**
 * Created by indexing on 16/4/19.
 */
public class WeixinEventMessage  extends WeixinMessage{
    private String event;

    public WeixinEventMessage() {
        setMsgType("event");
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
