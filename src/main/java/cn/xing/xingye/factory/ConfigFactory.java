package cn.xing.xingye.factory;

import cn.xing.xingye.model.WeixinConfig;
import cn.xing.xingye.service.BaseService;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by indexing on 16/4/13.
 */
public class ConfigFactory extends BaseService {
    final Logger log = LoggerFactory.getLogger(getClass());

    public WeixinConfig newWeixinConfig() {
        WeixinConfig config = new WeixinConfig();
        JSONObject json = queryConfig("weixin");
        if (json == null) return config;
        config.token = json.getString("Token");
        config.appID = json.getString("AppID");
        config.appSecret = json.getString("AppSecret");
        config.encodingAESKey = json.getString("EncodingAESKey");
        return config;
    }

    private JSONObject queryConfig(String configName) {
        JSONObject config = new JSONObject();
        JSONObject json = queryOne("select * from app_config where configName=?", configName);
        if (json == null) {
            log.error("not exist config: {}", configName);
            return config;
        }
        String s = json.getString("kvs");

        String[] kvs = s.split(",");
        for (String kv : kvs) {
            kv = kv.trim();
            String[] arr = kv.split(":");
            if (arr.length != 2) {
                log.error("invalid kv: {}", kv);
                continue;
            }
            config.put(arr[0].trim(), arr[1].trim());
        }

        log.info("load config {}: {}", configName, config);
        return config;
    }
}
