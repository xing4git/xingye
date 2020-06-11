package cn.xing.xingye.touzi.service;

import cn.xing.xingye.touzi.model.WeixinUser;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

/**
 * Created by indexing on 16/4/19.
 */
@Service
public class WeixinUserService extends BaseService {
    public void bind(String openId, Long userId) {
        jdbcTemplate.update("update weixin_user set userId=? where openId=?", userId, openId);
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

    public WeixinUser queryWxUser(String openId) {
        JSONObject queryUser = queryOne("select * from weixin_user where openId=?", openId);
        if (queryUser == null) return null;
        return toJavaObj(queryUser, WeixinUser.class);
    }

    public int queryWxUserCount() {
        JSONObject countJson = queryOne("select count(*) as userCount from weixin_user where deleted=0");
        if (countJson == null) return 0;
        return countJson.getInteger("userCount");
    }

    public void deleteWxUser(WeixinUser wxUser) {
        jdbcTemplate.update("update weixin_user set deleted=1 where openId=?", wxUser.getOpenId());
    }
}
