package cn.xing.xingye.touzi.exception;

/**
 * Created by indexing on 16/4/14.
 */
public class WeixinException extends Exception {
    public WeixinException() {
    }

    public WeixinException(String message) {
        super(message);
    }

    public WeixinException(String message, Throwable cause) {
        super(message, cause);
    }

    public WeixinException(Throwable cause) {
        super(cause);
    }

    public WeixinException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
