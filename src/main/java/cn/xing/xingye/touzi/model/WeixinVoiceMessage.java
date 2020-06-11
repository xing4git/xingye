package cn.xing.xingye.touzi.model;

/**
 * Created by indexing on 16/4/15.
 */
public class WeixinVoiceMessage extends WeixinMediaMessage {
    private String format;
    private String recognition;

    public WeixinVoiceMessage() {
        setMsgType("voice");
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getRecognition() {
        return recognition;
    }

    public void setRecognition(String recognition) {
        this.recognition = recognition;
    }
}
