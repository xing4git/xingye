package cn.xing.xingye.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangxing on 15/12/21.
 * 历史资产
 */
public class AssetHistory extends Asset {
    private static final Logger log = LoggerFactory.getLogger(AssetHistory.class);
    private Long assetId;

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }
}
