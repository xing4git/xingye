package cn.xing.xingye.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by indexing on 16/4/13.
 */
public class CookieUtils {
    public static String readTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        String token = null;
        for (Cookie c : cookies) {
            if (XingConst.TOKEN_COOKIE_NAME.equals(c.getName())) {
                token = c.getValue();
                break;
            }
        }
        return token;
    }

    public static void writeTokenCookie(HttpServletResponse response, String token) {
        // 低版本的servlet不支持httponly cookie, 采用Set-Cookie头进行模拟
        StringBuilder sb = new StringBuilder();
        sb.append(XingConst.TOKEN_COOKIE_NAME).append("=").append(token).append(";");
        sb.append("Path=/;");
        sb.append("Max-Age=").append(XingConst.TOKEN_EXPIRE_TIME).append(";");
        sb.append("HTTPOnly;");
        response.addHeader("Set-Cookie", sb.toString());
    }

    public static void removeTokenCookie(HttpServletResponse response) {
        Cookie c = new Cookie(XingConst.TOKEN_COOKIE_NAME, "");
        c.setMaxAge(0);
        response.addCookie(c);
    }
}
