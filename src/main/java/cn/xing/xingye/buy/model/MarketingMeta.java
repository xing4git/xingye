package cn.xing.xingye.buy.model;

/**
 * Created by indexing on 16/9/21.
 */
public class MarketingMeta {
    private Who who;
    private long startTime;
    private long endTime;
    private Channel channel;
    private Condition condition;
    private Entity entity;
    private Action action;
    private String desc;

    public enum Who {
        ALL, // 全网用户
        SHOP_MEMB, // 店铺会员
        DIRECT, // 定向人群
    }

    public enum Channel {
        ALL, // 全部渠道
        PC, // PC端
        APP, // APP端
    }

    public enum Condition {
        NONE, // 无门槛
        ENOUGH_MONEY, // 满元
        ENOUGH_MOUNT, // 满件
        MOUNT_LIMIT, // 限购
        TIME_LIMIT, // 限时
    }

    public enum Entity {
        SINGLE_ITEM, // 单个商品
        ITEMS, // 多个商品
        SHOP_ITEMS, // 店铺商品
    }

    public enum Action {
        DISCOUNT, // 折扣
        POST_FREE, // 包邮
        REDUCE_MONEY, // 减钱
    }
}
