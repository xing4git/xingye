package cn.xing.xingye.model;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by zhangxing on 15/12/21.
 */
public class BaseModel implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(BaseModel.class);
    private long id;
    private Timestamp created;
    private int deleted;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
