package cn.xing.xingye.buy.model;

public enum DateRangeEnum {
    /**
     * 月
     */
    MONTH("m"),
    /**
     * 季度
     */
    SEASON("jd"),
    /**
     * 年
     */
    YEAR("y");
    private String range;

    DateRangeEnum(String range) {
        this.range = range;
    }

    public String getRange() {
        return range;
    }
}
