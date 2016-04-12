package cn.xing.xingye.interceptor;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zhangxing on 15/12/10.
 */
public class LogRequestInterceptor extends HandlerInterceptorAdapter {
    private static final Logger log = LoggerFactory.getLogger(LogRequestInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String query = request.getQueryString();
        String url = request.getRequestURI() + (StringUtils.isEmpty(query) ? ""
                : "?" + query);
        if (!url.startsWith("/static")) {
            log.info("URL: {}", url);
        }
        return true;
    }

}
