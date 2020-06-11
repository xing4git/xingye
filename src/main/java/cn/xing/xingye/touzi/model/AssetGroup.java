package cn.xing.xingye.touzi.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangxing on 15/12/21.
 * 资产分组
 */
public class AssetGroup extends BaseModel {
    private static final Logger log = LoggerFactory.getLogger(AssetGroup.class);
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
