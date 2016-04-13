package cn.xing.xingye.interceptor;

import cn.xing.xingye.utils.XingConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by indexing on 16/4/13.
 * 登录拦截器
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {
    final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if (request.getAttribute(XingConst.KEY_LOGIN_USER) == null) {
            response.sendRedirect("/auth/login?redirect=" + request.getHeader("Referer"));
            return false;
        }

        return true;
    }


}
