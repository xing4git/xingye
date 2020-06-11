package cn.xing.xingye.touzi.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangxing on 15/12/23.
 */
public class ZhishuData extends BaseModel {
    private static final Logger log = LoggerFactory.getLogger(ZhishuData.class);
    private long zhishuId;
    private String dataDate;
    private double pe;
    private double pb;
    private int peRank;
    private int pbRank;
    private double shoupan;

    public long getZhishuId() {
        return zhishuId;
    }

    public void setZhishuId(long zhishuId) {
        this.zhishuId = zhishuId;
    }

    public String getDataDate() {
        return dataDate;
    }

    public void setDataDate(String dataDate) {
        this.dataDate = dataDate;
    }

    public double getPe() {
        return pe;
    }

    public void setPe(double pe) {
        this.pe = pe;
    }

    public double getPb() {
        return pb;
    }

    public void setPb(double pb) {
        this.pb = pb;
    }

    public int getPeRank() {
        return peRank;
    }

    public void setPeRank(int peRank) {
        this.peRank = peRank;
    }

    public int getPbRank() {
        return pbRank;
    }

    public void setPbRank(int pbRank) {
        this.pbRank = pbRank;
    }

    public double getShoupan() {
        return shoupan;
    }

    public void setShoupan(double shoupan) {
        this.shoupan = shoupan;
    }
}
