package cn.xing.xingye.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangxing on 15/12/9.
 */
public class XingConst {
    private static final Logger log = LoggerFactory.getLogger(XingConst.class);
    public static final String KEY_ERROR_MSG = "error_message";
    public static final String KEY_SUCCESS_MSG = "success_message";
    public static final String KEY_LOGIN_USER = "loginUser";

    public static final String TOKEN_COOKIE_NAME = "XyTOKEN";
    public static final int TOKEN_EXPIRE_TIME = 24 * 3600;


    public static final int BIGDECIMAL_SCALE = 3; // 保留小数位

    public static final String WEIXIN_GUIDE_CONTENT = "\n输入以下内容尝试以下吧: "
            + "zhishu: 获取各大指数的最新估值情况\n"
            + "sync: 同步指数(仅限绑定用户)\n"
            + "bind username password: 绑定用户(需要有网站账号)\n"
            + "还可以随意发送图片/语音/位置给我~~";
}
