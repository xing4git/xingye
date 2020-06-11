package cn.xing.xingye.buy.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 外资持股
 */
public class WaiZi {
    /**
     * 股票名
     */
    private String name;
    private String code;
    /**
     * 行业名
     */
    private String hyName;
    /**
     * 持股市值
     */
    private Double shareSZ;
    /**
     * 占流通股比例
     */
    private Double ltRate;
    /**
     * 变动市值
     */
    private Double incSZ;
    /**
     * 变动占流通股比例
     */
    private Double incLtRate;
    /**
     * 得分
     */
    private Double score;
    /**
     * 流通市值
     */
    private Double ltsz;
    /**
     * 总市值
     */
    private Double zsz;
    /**
     * 近半年减持比例
     */
    private Double jcRate;
    /**
     * 近半年增持比例
     */
    private Double zcRate;

    public Double getJcRate() {
        return jcRate;
    }

    public void setJcRate(Double jcRate) {
        this.jcRate = jcRate;
    }

    public Double getZcRate() {
        return zcRate;
    }

    public void setZcRate(Double zcRate) {
        this.zcRate = zcRate;
    }

    public Double getLtsz() {
        return ltsz;
    }

    public void setLtsz(Double ltsz) {
        this.ltsz = ltsz;
    }

    public Double getZsz() {
        return zsz;
    }

    public void setZsz(Double zsz) {
        this.zsz = zsz;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHyName() {
        return hyName;
    }

    public void setHyName(String hyName) {
        this.hyName = hyName;
    }

    public Double getShareSZ() {
        return shareSZ;
    }

    public void setShareSZ(Double shareSZ) {
        this.shareSZ = shareSZ;
    }

    public Double getLtRate() {
        return ltRate;
    }

    public void setLtRate(Double ltRate) {
        this.ltRate = ltRate;
    }

    public Double getIncSZ() {
        return incSZ;
    }

    public void setIncSZ(Double incSZ) {
        this.incSZ = incSZ;
    }

    public Double getIncLtRate() {
        return incLtRate;
    }

    public void setIncLtRate(Double incLtRate) {
        this.incLtRate = incLtRate;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
