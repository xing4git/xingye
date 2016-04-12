package cn.xing.xingye.editor;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by indexing on 16/3/7.
 */
public class TimestampEditor extends PropertyEditorSupport {
    private static final Logger log = LoggerFactory.getLogger(TimestampEditor.class);
    private SimpleDateFormat format;

    public TimestampEditor(SimpleDateFormat format) {
        this.format = format;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        log.info("parse %s to timestamp", text);
        if (StringUtils.isEmpty(text)) {
            setValue(null);
            return;
        }
        try {
            setValue(new Timestamp(format.parse(text).getTime()));
        } catch (ParseException e) {
            throw new IllegalArgumentException("parse error: " + text, e);
        }
    }

}
