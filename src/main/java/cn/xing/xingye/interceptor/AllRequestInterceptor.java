package cn.xing.xingye.interceptor;

import cn.xing.xingye.model.LoginInfo;
import cn.xing.xingye.service.LoginService;
import cn.xing.xingye.utils.CookieUtils;
import cn.xing.xingye.utils.XingConst;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zhangxing on 15/12/10.
 * 拦截所有请求
 */
public class AllRequestInterceptor extends HandlerInterceptorAdapter {
    private static final Logger log = LoggerFactory.getLogger(AllRequestInterceptor.class);
    @Autowired
    private LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String query = request.getQueryString();
        String url = request.getRequestURI() + (StringUtils.isEmpty(query) ? ""
                : "?" + query);

        String token = CookieUtils.readTokenCookie(request);
        if (StringUtils.isEmpty(token)) return true;
        LoginInfo loginInfo = loginService.queryLoginInfoByToken(token);
        if (loginInfo == null) return true;

        // 过期时间判断
        long now = System.currentTimeMillis();
        if (now - loginInfo.getLoginTime() > XingConst.TOKEN_EXPIRE_TIME * 1000L) {
            CookieUtils.removeTokenCookie(response);
            loginService.removeLoginInfoFromCache(token);
            return true;
        }

        request.setAttribute(XingConst.KEY_LOGIN_USER, loginInfo.getUser());
        String username = loginInfo == null ? null : loginInfo.getUser().getUsername();
        if (!url.startsWith("/static")) {
            log.info("URL: {}, username: {}", url, username);
        }
        return true;
    }

}
