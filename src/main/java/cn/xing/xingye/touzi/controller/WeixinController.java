package cn.xing.xingye.touzi.controller;

import cn.xing.xingye.touzi.exception.WeixinException;
import cn.xing.xingye.touzi.model.*;
import cn.xing.xingye.touzi.service.WeixinService;
import cn.xing.xingye.touzi.utils.WeixinMessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by indexing on 16/4/13.
 */
@Controller
@RequestMapping("/weixin")
public class WeixinController {
    final Logger LOG = LoggerFactory.getLogger(getClass());
    @Autowired
    private WeixinService weixinService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public void checkSign(@RequestParam("signature") String signature,
                          @RequestParam("timestamp") String timestamp,
                          @RequestParam("nonce") String nonce,
                          @RequestParam("echostr") String echostr,
                          HttpServletResponse response)
            throws IOException {
        if (!weixinService.checkSignature(signature, timestamp, nonce)) {
            LOG.error("check weixin signature failed!");
            return;
        }

        LOG.info("check weixin signature succ! echo: {}", echostr);
        PrintWriter writer = response.getWriter();
        writer.write(echostr);
        writer.flush();
        return;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public void receiveMsg(@RequestParam("signature") String signature,
                             @RequestParam("timestamp") String timestamp,
                             @RequestParam("nonce") String nonce,
                             @RequestBody String content, HttpServletResponse response)
            throws Exception {
        if (!weixinService.checkSignature(signature, timestamp, nonce)) {
            LOG.error("check weixin signature failed!");
            return;
        }

        try {
            WeixinMessage receiveMessage = WeixinMessageHelper.readFromXML(content);
            WeixinMessage responseMessage = weixinService.response(receiveMessage);
            if (responseMessage == null) return;
            String xml = WeixinMessageHelper.message2XML(responseMessage);
            xml = xml.trim();
            LOG.info("response weixin message: {}", xml);
            PrintWriter writer = response.getWriter();
            writer.write(xml);
            writer.flush();
        } catch (Exception e) {
            LOG.error("response weixin message error", e);
        }
    }

    @ResponseBody
    @RequestMapping("sync_weixin_user")
    public void syncWeixinUser() throws WeixinException {
         weixinService.syncWxUser();
    }

    @ResponseBody
    @RequestMapping("access_token")
    public String accessToken() throws WeixinException {
        return weixinService.readAccessToken().accessToken;
    }

    @ResponseBody
    @RequestMapping("create_menu")
    public String createMenu() throws WeixinException {
        WeixinMenu menu = new WeixinMenu();

        WeixinButtonGroup bookGroup = new WeixinButtonGroup("书籍推荐");
        bookGroup.addButton(new WeixinViewButton("指数基金投资", "http://www.duokan.com/book/101329"));
        bookGroup.addButton(new WeixinViewButton("投资最重要的事", "http://www.duokan.com/book/24392"));
        bookGroup.addButton(new WeixinViewButton("资产配置", "http://www.duokan.com/book/98831"));

        menu.addButton(bookGroup);
        weixinService.createMenu(menu);
        return "succ";
    }
}
