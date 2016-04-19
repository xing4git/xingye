package cn.xing.xingye.service;

import cn.xing.xingye.model.WeixinUser;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

/**
 * Created by indexing on 16/4/19.
 */
@Service
public class WeixinUserService extends BaseService {
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
}
