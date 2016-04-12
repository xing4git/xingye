package cn.xing.xingye;

import cn.xing.xingye.utils.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangxing on 15/12/2.
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        String s = HttpClientUtils.get("http://www.swsindex.com/downloadfiles.aspx?swindexcode=801812&type=510&columnid=8890","utf8");
        log.info(s);
    }
}
