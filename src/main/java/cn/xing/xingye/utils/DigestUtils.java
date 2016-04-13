package cn.xing.xingye.utils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

/**
 * Created by indexing on 16/4/13.
 */
public class DigestUtils {
    static final Logger LOG = LoggerFactory.getLogger(DigestUtils.class);

    private static char[] md5s = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String md5(String source) {
        if (source == null) return null;
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(source.getBytes("utf8"));
            for (byte b : bytes) {
                int high4 = b >>> 4 & 0xf;
                int low4 = b & 0xf;
                sb.append(md5s[high4]).append(md5s[low4]);
            }
        } catch (Exception e) {
            LOG.error("md5 error", e);
        }
        return sb.toString();
    }

    public static String sha1(String source) {
        if (source == null) return null;
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = md.digest(source.getBytes("utf8"));
            for (byte b : bytes) {
                int high4 = b >>> 4 & 0xf;
                int low4 = b & 0xf;
                sb.append(md5s[high4]).append(md5s[low4]);
            }
        } catch (Exception e) {
            LOG.error("digest error", e);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(Base64.encodeBase64String(DigestUtils.md5("shuai1989912").getBytes()));
    }
}
