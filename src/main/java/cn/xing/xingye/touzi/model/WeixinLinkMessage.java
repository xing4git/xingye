package cn.xing.xingye.touzi.model;

/**
 * Created by indexing on 16/4/15.
 */
public class WeixinLinkMessage extends WeixinMessage {
    private String title;
    private String description;
    private String url;

    public WeixinLinkMessage() {
        setMsgType("link");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
