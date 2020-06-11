package cn.xing.xingye.touzi.utils;

import cn.xing.xingye.touzi.model.WeixinTextMessage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by indexing on 16/4/15.
 */
public class XMLUtils {
    static final Logger LOG = LoggerFactory.getLogger(XMLUtils.class);

    public static JSONObject xml2Json(String xml) throws Exception {
        Document doc = DocumentHelper.parseText(xml);
        Element root = doc.getRootElement();
        return element2Json(root);
    }

    public static String obj2Xml(Object obj, String root) throws Exception {
        String text = JSON.toJSONString(obj);
        JSONObject json = JSON.parseObject(text);
        return json2Xml(json, root);
    }

    public static String json2Xml(JSONObject json, String root) throws Exception {
        Document doc = DocumentHelper.createDocument();
        Element rootElement = DocumentHelper.createElement(root);
        doc.setRootElement(rootElement);
        doc.setDocType(null);
        doJson(json, rootElement);
        return doc.asXML();
    }

    private static void doJson(JSONObject json, Element rootElement) {
        for (String key : json.keySet()) {
            Object obj = json.get(key);
            Element elem = DocumentHelper.createElement(key);
            rootElement.add(elem);
            if (obj == null) {
                continue;
            }
            if (obj instanceof String) {
                elem.add(DocumentHelper.createCDATA(obj.toString()));
                continue;
            }
            if (obj instanceof Number || obj instanceof Boolean || obj instanceof Character) {
                elem.setText(obj.toString());
                continue;
            }
            if (obj instanceof JSONObject) {
                doJson((JSONObject) obj, elem);
                continue;
            }
            LOG.error("unsupported object type, {} -> {}", key, obj);
        }
    }

    private static JSONObject element2Json(Element root) {
        List elems = root.elements();
        String name = root.getName();
        JSONObject json = new JSONObject();
        if (elems.isEmpty()) {
            json.put(name, root.getTextTrim());
            return json;
        }

        JSONObject childs = new JSONObject();
        for (Object obj : elems) {
            Element e = (Element) obj;
            JSONObject innerJson = element2Json(e);
            childs.putAll(innerJson);
        }
        json.put(name, childs);
        return json;
    }

    public static void main(String[] args) throws Exception {
        String xml = "<xml><ToUserName><![CDATA[gh_c7c280f400a0]]></ToUserName>\n" +
                "<FromUserName><![CDATA[oQc4uwG6f2bOHqtPxsGA7sBSjBJ8]]></FromUserName>\n" +
                "<CreateTime>1460625742</CreateTime>\n" +
                "<MsgType><![CDATA[text]]></MsgType>\n" +
                "<Content><![CDATA[hello]]></Content>\n" +
                "<MsgId>6273339793988066387</MsgId>\n" +
                "</xml>";
        System.out.println(WeixinMessageHelper.readFromXML(xml));

        WeixinTextMessage message = new WeixinTextMessage();
        message.setContent("hello");
        System.out.println(WeixinMessageHelper.message2XML(message));
    }
}
