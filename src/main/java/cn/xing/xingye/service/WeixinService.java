package cn.xing.xingye.service;

import cn.xing.xingye.exception.WeixinException;
import cn.xing.xingye.model.*;
import cn.xing.xingye.utils.HttpClientUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by indexing on 16/4/14.
 */
@Service
public class WeixinService extends BaseService {
    final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private WeixinConfig config;
    private WeixinAccessToken accessToken;

    private String serverURL = "https://api.weixin.qq.com/cgi-bin";

    public boolean checkSignature(String signature, String timestamp, String nonce) {
        List<String> strs = Lists.newArrayList(config.token, timestamp, nonce);
        Collections.sort(strs);
        String s = StringUtils.collectionToDelimitedString(strs, "");
        s = DigestUtils.shaHex(s);

        if (s.equals(signature)) {
            return true;
        }
        return false;
    }

    public WeixinAccessToken readAccessToken() throws WeixinException {
        if (accessToken != null && !accessToken.isExpired()) {
            return accessToken;
        }

        return updateAccessToken();
    }

    public synchronized WeixinAccessToken updateAccessToken() throws WeixinException {
        String url = serverURL + "/token?grant_type=client_credential&appid="
                + config.appID + "&secret=" + config.appSecret;
        try {
            JSONObject json = JSON.parseObject(new String(HttpClientUtils.get(url)));
            accessToken = new WeixinAccessToken();
            accessToken.accessToken = json.getString("access_token");
            accessToken.created = System.currentTimeMillis();
            accessToken.expireTime = accessToken.created + json.getInteger("expires_in") * 1000L;
            LOG.info("access token: {}", accessToken.accessToken);
            return accessToken;
        } catch (Exception e) {
            if (e instanceof WeixinException) throw (WeixinException) e;
            throw new WeixinException("request access token error", e);
        }
    }

    public WeixinMessage response(WeixinMessage receiveMessage) {
        WeixinTextMessage response = new WeixinTextMessage();
        response.setFromUserName(receiveMessage.getToUserName());
        response.setToUserName(receiveMessage.getFromUserName());
        response.setCreateTime((int) (System.currentTimeMillis() / 1000));

        String content = null;
        if (receiveMessage instanceof WeixinTextMessage) {
            content = handleTextMessage(((WeixinTextMessage) receiveMessage).getContent());
        } else if (receiveMessage instanceof WeixinVoiceMessage) {
            String recognition = ((WeixinVoiceMessage) receiveMessage).getRecognition();
            if (StringUtils.isEmpty(recognition)) {
                content = "";
            } else {
                content = handleTextMessage(content);
            }
        } else if (receiveMessage instanceof WeixinImageMessage) {
            content = "这是...1024的图吗?";
        } else if (receiveMessage instanceof WeixinLocationMessage) {
            content = ((WeixinLocationMessage) receiveMessage).getLabel() + "这是我们的约会地址吗?";
        } else if (receiveMessage instanceof WeixinVideoMessage) {
            content = "视频什么的, 我最喜欢了";
        } else if (receiveMessage instanceof WeixinLinkMessage) {
            content = "文章已珍藏, 如果是1024的文章, 我会给你满分.";
        } else {
            content = "哎呀, 虽然不知道你给我发了什么, 但看起来很厉害的样子";
        }
        response.setContent(content);

        return response;
    }

    private String handleTextMessage(String content) {
        return "hello world: " + content;
    }

    public void createMenu(WeixinMenu menu) throws WeixinException {
        String api = "/menu/create";
        requestApi(api, menu);
    }

    private String requestApi(String api, Object body) throws WeixinException {
        String url = serverURL + api + "?access_token=" + readAccessToken().accessToken;
        String postContent = JSON.toJSONString(body);
        try {
            String res = HttpClientUtils.post(url, postContent, "utf8");
            JSONObject json = JSON.parseObject(res);
            int errcode = json.getInteger("errcode");
            if (errcode != 0) {
                throw new WeixinException(String.format("errcode: %s, errmsg: %s, api: %s, post: %s",
                        errcode, json.getString("errmsg"), api, postContent));
            }
            return res;
        } catch (Exception e) {
            if (e instanceof WeixinException) throw (WeixinException) e;
            throw new WeixinException(String.format("request weixin error, api: %s, post: %s", api, postContent), e);
        }
    }

}
