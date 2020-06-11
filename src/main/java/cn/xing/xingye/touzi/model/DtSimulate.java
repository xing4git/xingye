package cn.xing.xingye.touzi.model;

import java.math.BigDecimal;

/**
 * Created by indexing on 16/5/19.
 * 定投模拟投入
 */
public class DtSimulate extends BaseModel {
    private double value; // 指数点位
    private int month; // 月份
    private double touru = 0; // 当月投入
    private double totalTouru = 0; // 累计总投入
    private double jingzhi = 0; // 净值

    public double getValue() {
        return new BigDecimal(value).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public double getTouru() {
        return new BigDecimal(touru).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
    }

    public void setTouru(double touru) {
        this.touru = touru;
    }

    public double getTotalTouru() {
        return new BigDecimal(totalTouru).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
    }

    public void setTotalTouru(double totalTouru) {
        this.totalTouru = totalTouru;
    }

    public double getJingzhi() {
        return new BigDecimal(jingzhi).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
    }

    public void setJingzhi(double jingzhi) {
        this.jingzhi = jingzhi;
    }

    public void addJingzhi(double jingzhi) {
        this.jingzhi += jingzhi;
    }

    public double getYingli() {
        return new BigDecimal(jingzhi - totalTouru).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
    }

    public double getYingliFudu() {
        if (totalTouru > 0) {
            return new BigDecimal(getYingli() * 100 / totalTouru).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        }
        return 0;
    }
}
