package cn.xing.xingye.touzi.service;

import cn.xing.xingye.touzi.exception.LoginException;
import cn.xing.xingye.touzi.model.LoginInfo;
import cn.xing.xingye.touzi.model.User;
import com.google.common.collect.Maps;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by indexing on 16/4/13.
 */
@Service
public class LoginService extends BaseService {
    final Logger LOG = LoggerFactory.getLogger(getClass());

    private Random random = new Random();
    private ConcurrentMap<Long, ConcurrentMap<String, LoginInfo>> loginInfoCache = Maps.newConcurrentMap();

    public LoginInfo queryLoginInfoByToken(String token) {
        if (token == null) return null;
        Long userId = readUserIdFromToken(token);
        if (userId == null) return null;
        return queryLoginInfoFromCache(userId, token);
    }

    public Long readUserIdFromToken(String token) {
        try {
            int idx = token.indexOf("_");
            if (idx <= 0 || idx == token.length() - 1) return null;
            return Long.valueOf(token.substring(idx + 1));
        } catch (Exception e) {
            LOG.error("read userId from cookie error", e);
            return null;
        }
    }

    public LoginInfo login(String userIdentity, String password) throws LoginException {
        if (userIdentity == null || password == null) {
            throw new LoginException("用户名或密码不能为空");
        }
        try {
            password = encodePassword(password);
            User u;
            if (userIdentity.matches("^\\d+$")) {
                u = queryUserById(Long.valueOf(userIdentity));
            } else if (userIdentity.contains("@")) {
                u = queryUserByEmail(userIdentity);
            } else {
                u = queryUserByName(userIdentity);
            }
            if (u == null) {
                throw new LoginException("用户不存在");
            }

            if (!password.equals(u.getPassword())) {
                LOG.info("user {} password not match, in db: {}, input: {}", userIdentity, u.getPassword(), password);
                throw new LoginException("密码错误");
            }

            LoginInfo loginInfo = genLoginInfo(u);
            writeLoginInfoToCache(loginInfo);
            return loginInfo;
        } catch (LoginException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("login error", e);
            throw new LoginException("登录出错", e);
        }
    }

    public void removeLoginInfoFromCache(String token) {
        Long userId = readUserIdFromToken(token);
        if (userId == null) return;
        ConcurrentMap<String, LoginInfo> tokenMap = loginInfoCache.get(userId);
        if (tokenMap == null) return;
        tokenMap.remove(token);
    }

    private LoginInfo queryLoginInfoFromCache(Long userId, String token) {
        ConcurrentMap<String, LoginInfo> tokenMap = loginInfoCache.get(userId);
        if (tokenMap == null) return null;
        return tokenMap.get(token);
    }

    private void writeLoginInfoToCache(LoginInfo loginInfo) {
        Long userId = loginInfo.getUser().getId();
        ConcurrentMap<String, LoginInfo> tokenMap = Maps.newConcurrentMap();
        ConcurrentMap<String, LoginInfo> oldTokenMap = loginInfoCache.putIfAbsent(userId, tokenMap);
        if (oldTokenMap != null) {
            tokenMap = oldTokenMap;
        }
        tokenMap.put(loginInfo.getToken(), loginInfo);
    }

    private String encodePassword(String password) {
        return DigestUtils.md5Hex(password);
    }

    public User queryUserById(Long userId) {
        return toJavaObj(queryOne("select * from user where id=?", userId), User.class);
    }

    public User queryUserByName(String username) {
        return toJavaObj(queryOne("select * from user where username=?", username), User.class);
    }

    public User queryUserByEmail(String email) {
        return toJavaObj(queryOne("select * from user where email=?", email), User.class);
    }

    private String genToken(User u) {
        StringBuilder sb = new StringBuilder();
        sb.append(u.getEmail()).append("_").append(System.currentTimeMillis())
                .append("_").append(random.nextLong());
        String md5Encode = DigestUtils.md5Hex(sb.toString());
        String token = md5Encode + "_" + u.getId();
        return token;
    }

    private LoginInfo genLoginInfo(User u) {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUser(u);
        loginInfo.setToken(genToken(u));
        loginInfo.setLoginTime(System.currentTimeMillis());
        return loginInfo;
    }


}
