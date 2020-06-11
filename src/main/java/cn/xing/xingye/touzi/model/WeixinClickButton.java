package cn.xing.xingye.touzi.model;

/**
 * Created by indexing on 16/4/14.
 */
public class WeixinClickButton extends WeixinButton {
    private String key;

    public WeixinClickButton() {
    }

    public WeixinClickButton(String name, String key) {
        super(name);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return "click";
    }
}
