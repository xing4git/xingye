package cn.xing.xingye.service;

import cn.xing.xingye.model.AssetGroup;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by zhangxing on 15/12/21.
 */
@Service
public class AssetService {
    private static final Logger log = LoggerFactory.getLogger(AssetService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JSONArray query(String sql, Object... params) {
        return jdbcTemplate.query(sql, params, new ResultSetExtractor<JSONArray>() {
            @Override
            public JSONArray extractData(ResultSet rs)
                    throws SQLException, DataAccessException {
                JSONArray rows = new JSONArray();
                ResultSetMetaData metaData = rs.getMetaData();
                // 从1开始
                int column = metaData.getColumnCount();
                while (rs.next()) {
                    JSONObject row = new JSONObject();
                    for (int i = 1; i <= column; i++) {
                        Object v = rs.getObject(i);
                        String k = metaData.getColumnName(i);
                        row.put(k, v);
                    }
                    rows.add(row);
                }
                return rows;
            }
        });
    }

    public JSONObject queryOne(String sql, Object... params) {
        JSONArray rows = query(sql, params);
        if (rows.isEmpty()) return null;
        return rows.getJSONObject(0);
    }

    public void addGroup(String name) {
        jdbcTemplate.update("INSERT INTO asset_group(name) VALUES (?)", name);
    }

    public List<AssetGroup> queryGroups() {
        JSONArray rows = query("select * from asset_group where deleted=0");
        return toJavaList(rows, AssetGroup.class);
    }

    private <T> List<T> toJavaList(JSONArray arr, Class<T> clazz) {
        List<T> list = Lists.newArrayList();
        for (int i = 0; i < arr.size(); i++) {
            list.add(JSON.toJavaObject(arr.getJSONObject(i), clazz));
        }
        return list;
    }
}
