package cn.xing.xingye.touzi.utils;

import cn.xing.xingye.touzi.model.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by indexing on 16/4/14.
 */
public class WeixinMessageHelper {
    static final Logger LOG = LoggerFactory.getLogger(WeixinMessageHelper.class);

    public static WeixinMessage readFromXML(String xml) throws Exception {
        JSONObject json = XMLUtils.xml2Json(xml);
        json = json.getJSONObject("xml");
        json = uncapitalizeKey(json);
        LOG.info("weixin message json: {}", json.toJSONString());

        String msgType = json.getString("msgType");
        Class<? extends WeixinMessage> messageClass = messageClass(msgType);
        if (messageClass == null) throw new Exception("unkown message type: " + msgType);
        return JSON.toJavaObject(json, messageClass);
    }

    public static String message2XML(WeixinMessage message) throws Exception {
        String text = JSON.toJSONString(message);
        JSONObject json = JSON.parseObject(text);
        json = capitalizeKey(json);
        String xml = XMLUtils.json2Xml(json, "xml");
        String prefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        if (xml.startsWith(prefix)) {
            xml = xml.substring(prefix.length());
        }
        return xml;
    }

    private static Class<? extends WeixinMessage> messageClass(String msgType) {
        if ("text".equals(msgType)) return WeixinTextMessage.class;
        if ("image".equals(msgType)) return WeixinImageMessage.class;
        if ("voice".equals(msgType)) return WeixinVoiceMessage.class;
        if ("video".equals(msgType)) return WeixinVideoMessage.class;
        if ("shortvideo".equals(msgType)) return WeixinVideoMessage.class;
        if ("location".equals(msgType)) return WeixinLocationMessage.class;
        if ("link".equals(msgType)) return WeixinLinkMessage.class;
        if ("event".equals(msgType)) return WeixinEventMessage.class;
        return null;
    }

    private static JSONObject uncapitalizeKey(JSONObject json) {
        JSONObject json2 = new JSONObject();
        for (String key : json.keySet()) {
            // 首字母小写
            String key2 = StringUtils.uncapitalize(key);
            json2.put(key2, json.get(key));
        }
        return json2;
    }

    private static JSONObject capitalizeKey(JSONObject json) {
        JSONObject json2 = new JSONObject();
        for (String key : json.keySet()) {
            // 首字母大写
            String key2 = StringUtils.capitalize(key);
            json2.put(key2, json.get(key));
        }
        return json2;
    }


}
