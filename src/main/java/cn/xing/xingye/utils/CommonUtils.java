package cn.xing.xingye.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * Created by indexing on 16/4/12.
 */
public class CommonUtils {
    static final Logger log = LoggerFactory.getLogger(CommonUtils.class);
    public static final String ZHISHU_DATE_PATTERN = "yyyy-MM-dd";

    public static long zhishuDateToTimestamp(String date) {
        if (!isValidZhishuDate(date)) {
            throw new RuntimeException("not valid zhishu date: " + date + ", not match yyyy-MM-dd");
        }
        SimpleDateFormat format = new SimpleDateFormat(ZHISHU_DATE_PATTERN);
        try {
            return format.parse(date).getTime();
        } catch (Exception e) {
            log.error("parse {} to time error", date, e);
            throw new RuntimeException("parse " + date + " to timestamp error");
        }
    }

    public static boolean isValidZhishuDate(String date) {
        if (StringUtils.isEmpty(date)) return false;
        if (!date.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
            return false;
        }
        return true;
    }
}
