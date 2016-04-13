package cn.xing.xingye.controller;

import cn.xing.xingye.model.LoginInfo;
import cn.xing.xingye.service.LoginService;
import cn.xing.xingye.utils.CookieUtils;
import cn.xing.xingye.utils.XingConst;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by indexing on 16/4/13.
 */
@Controller
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private LoginService loginService;

    @RequestMapping("login")
    public String login() {
        return "auth/login";
    }

    @RequestMapping("login_action")
    public String loginAction(HttpServletRequest request, HttpServletResponse response,
                              @RequestParam("userIdentity") String userIdentity,
                              @RequestParam("password") String password,
                              @RequestParam(value = "redirect", required = false) String redirect,
                              RedirectAttributes attr) {
        try {
            LoginInfo loginInfo = loginService.login(userIdentity, password);
            CookieUtils.writeTokenCookie(response, loginInfo.getToken());
            request.setAttribute(XingConst.KEY_LOGIN_USER, loginInfo.getUser());
            if (StringUtils.isNotBlank(redirect)) {
                return "redirect:" + redirect;
            }
        } catch (Exception e) {
            attr.addAttribute(XingConst.KEY_ERROR_MSG, e.getMessage());
            return "redirect:/auth/login?redirect=" + redirect;
        }
        return "redirect:/";
    }

    @RequestMapping("logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        String token = CookieUtils.readTokenCookie(request);
        if (token == null) {
            return "redirect:/";
        }
        CookieUtils.removeTokenCookie(response);
        loginService.removeLoginInfoFromCache(token);
        return "redirect:/";
    }


}
