package cn.xing.xingye.touzi.model;

/**
 * Created by indexing on 16/4/14.
 */
public class WeixinViewButton extends WeixinButton {
    private String url;

    public WeixinViewButton() {
    }

    public WeixinViewButton(String name, String url) {
        super(name);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return "view";
    }
}
