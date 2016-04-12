package cn.xing.xingye.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhangxing on 15/12/7.
 */
public class HttpRequestUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpRequestUtils.class);

    public static Integer getIntParam(HttpServletRequest request, String paramName, Integer defaultValue) {
        String value = request.getParameter(paramName);
        if (StringUtils.isEmpty(value)) return defaultValue;
        return Integer.valueOf(value);
    }

    public static Long getLongParam(HttpServletRequest request, String paramName, Long defaultValue) {
        String value = request.getParameter(paramName);
        if (StringUtils.isEmpty(value)) return defaultValue;
        return Long.valueOf(value);
    }

    public static Double getDoubleParam(HttpServletRequest request, String paramName, Double defaultValue) {
        String value = request.getParameter(paramName);
        if (StringUtils.isEmpty(value)) return defaultValue;
        return Double.valueOf(value);
    }

    public static String getStringParam(HttpServletRequest request, String paramName, String defaultValue) {
        String value = request.getParameter(paramName);
        if (StringUtils.isEmpty(value)) return defaultValue;
        return value;
    }

}
