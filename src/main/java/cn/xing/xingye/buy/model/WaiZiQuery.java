package cn.xing.xingye.buy.model;

public class WaiZiQuery {
    /**
     * 请求指定日期的数据
     */
    public String date;
    /**
     * 变动日期范围
     */
    public DateRangeEnum incDateRange;
    /**
     * 股票数
     */
    public int dataNum;
    /**
     * 过滤流通比例小于指定值的股票
     */
    public double filterLtRate;
    /**
     * 过滤减持比例小于指定值的股票
     */
    public double filterJcRate;

    /**
     * 得分前N的股票补充增减持信息
     */
    public int zjcTopN;
    /**
     * 增减持信息优先读取缓存
     */
    public boolean zjcUseCacheFirst;

    /**
     * 市值排序因子
     */
    public double factorShareSZ;
    /**
     * 占流通股比例因子
     */
    public double factorLtRate;
    /**
     * 持有市值变动因子
     */
    public double factorIncSZ;
    /**
     * 占流通股比例变动因子
     */
    public double factorIncLtRate;
}
