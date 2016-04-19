package cn.xing.xingye.model;

/**
 * Created by indexing on 16/4/15.
 */
public class WeixinLocationMessage extends WeixinMessage {
    private Double locationX;
    private Double locationY;
    private Double scale;
    private String label;

    public WeixinLocationMessage() {
        setMsgType("location");
    }

    public Double getLocationX() {
        return locationX;
    }

    public void setLocationX(Double locationX) {
        this.locationX = locationX;
    }

    public Double getLocationY() {
        return locationY;
    }

    public void setLocationY(Double locationY) {
        this.locationY = locationY;
    }

    public Double getScale() {
        return scale;
    }

    public void setScale(Double scale) {
        this.scale = scale;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
