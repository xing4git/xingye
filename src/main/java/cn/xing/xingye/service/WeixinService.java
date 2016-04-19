package cn.xing.xingye.service;

import cn.xing.xingye.exception.WeixinException;
import cn.xing.xingye.model.*;
import cn.xing.xingye.utils.HttpClientUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
                content = "没有听懂说什么呢? 不会是你的呻吟吧...";
            } else {
                content = handleTextMessage(content);
            }
        } else if (receiveMessage instanceof WeixinImageMessage) {
            content = "这是...1024的图吗?";
        } else if (receiveMessage instanceof WeixinLocationMessage) {
            content = ((WeixinLocationMessage) receiveMessage).getLabel() + ": 这是我们的约会地址吗?";
        } else if (receiveMessage instanceof WeixinVideoMessage) {
            content = "视频什么的, 我最喜欢了";
        } else if (receiveMessage instanceof WeixinLinkMessage) {
            content = "文章已珍藏, 如果是1024的文章, 我会给你满分.";
        } else if (receiveMessage instanceof WeixinEventMessage) {
            String event = ((WeixinEventMessage) receiveMessage).getEvent();
            if ("subscribe".equals(event)) {
                WeixinUser wxUser = new WeixinUser(receiveMessage.getFromUserName());
                addWxUser(wxUser);
                int userCount = queryWxUserCount();
                content = "你才来啊, 在你之前已经有" + userCount + "位同道关注我啦!";
            } else if ("unsubscribe".equals(event)) {
                WeixinUser wxUser = new WeixinUser(receiveMessage.getFromUserName());
                deleteWxUser(wxUser);
                return null;
            } else {
                return null;
            }
        } else {
            content = "哎呀, 虽然不知道你给我发了什么, 但看起来很厉害的样子";
        }
        response.setContent(content);

        return response;
    }

    public void addWxUser(WeixinUser wxUser) {
        JSONObject queryUser = queryOne("select * from weixin_user where openId=?", wxUser.getOpenId());
        if (queryUser == null) {
            jdbcTemplate.update("insert into weixin_user(openId) values(?)", wxUser.getOpenId());
            return;
        }
        WeixinUser oldUser = toJavaObj(queryUser, WeixinUser.class);
        if (oldUser.getDeleted() == 1) {
            jdbcTemplate.update("update weixin_user set deleted=0 where openId=?", wxUser.getOpenId());
        }
    }

    public int queryWxUserCount() {
        JSONObject countJson = queryOne("select count(*) as userCount from weixin_user where deleted=0");
        if (countJson == null) return 0;
        return countJson.getInteger("userCount");
    }

    public void deleteWxUser(WeixinUser wxUser) {
        jdbcTemplate.update("update weixin_user set deleted=1 where openId=?", wxUser.getOpenId());
    }

    public void syncWxUser() throws WeixinException {
        Set<String> openIds = requestOpenIds("");
        for (String openId : openIds) {
            addWxUser(new WeixinUser(openId));
        }
        log.info("add {} weixin users to db", openIds.size());
    }

    private Set<String> requestOpenIds(String nextOpenId) throws WeixinException {
        Set<String> openIds = Sets.newHashSet();
        Map<String, String> urlParams = Maps.newHashMap();
        urlParams.put("next_openid", nextOpenId);
        String res = requestApiGet("/user/get", urlParams);

        if (StringUtils.isEmpty(res)) return openIds;
        JSONObject json = JSON.parseObject(res);
        if (json.get("data") == null) return openIds;
        JSONArray arr = json.getJSONObject("data").getJSONArray("openid");
        if (arr == null) return openIds;

        for (int i = 0; i < arr.size(); i++) {
            String openId = arr.getString(i);
            if (StringUtils.isEmpty(openId)) continue;
            openIds.add(openId);
        }

        String newNextOpenId = json.getString("next_openid");
        if (!StringUtils.isEmpty(newNextOpenId)) {
            openIds.addAll(requestOpenIds(newNextOpenId));
        }
        return openIds;
    }

    private String handleTextMessage(String content) {
        return "hello world: " + content;
    }

    public void createMenu(WeixinMenu menu) throws WeixinException {
        String api = "/menu/create";
        requestApiPost(api, menu);
    }

    private String requestApiGet(String api, Map<String,String> urlParams) throws WeixinException {
        String url = serverURL + api + "?access_token=" + readAccessToken().accessToken;
        if (urlParams != null) {
            for (String key : urlParams.keySet()) {
                url += "&" + key + "=" + urlParams.get(key);
            }
        }
        log.info("request api: {}", url);

        try {
            String res = HttpClientUtils.get(url, "utf8");
            return checkResponseErrcode(url, res);
        } catch (Exception e) {
            if (e instanceof WeixinException) throw (WeixinException) e;
            throw new WeixinException(String.format("request weixin error, url: %s", url), e);
        }
    }

    private String requestApiPost(String api, Object body) throws WeixinException {
        String url = serverURL + api + "?access_token=" + readAccessToken().accessToken;
        String postContent = null;
        if (body != null) {
            postContent = JSON.toJSONString(body);
        }
        try {
            String res = HttpClientUtils.post(url, postContent, "utf8");
            return checkResponseErrcode(url, res);
        } catch (Exception e) {
            if (e instanceof WeixinException) throw (WeixinException) e;
            throw new WeixinException(String.format("request weixin error, api: %s, post: %s", api, postContent), e);
        }
    }

    private String checkResponseErrcode(String url, String res) throws WeixinException {
        JSONObject json = JSON.parseObject(res);
        if (!json.containsKey("errcode")) return res;
        int errcode = json.getInteger("errcode");
        if (errcode != 0) {
            throw new WeixinException(String.format("errcode: %s, errmsg: %s, url: %s",
                    errcode, json.getString("errmsg"), url));
        }
        return res;
    }

}
