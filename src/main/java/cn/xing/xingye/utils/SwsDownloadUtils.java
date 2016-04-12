package cn.xing.xingye.utils;

import cn.xing.xingye.model.ZhishuData;
import com.google.common.collect.Lists;
import org.apache.http.client.methods.HttpGet;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by indexing on 16/4/12.
 */
public class SwsDownloadUtils {
    static final Logger log = LoggerFactory.getLogger(SwsDownloadUtils.class);

    public static List<ZhishuData> parse(String swsCode, Long zhishuId) {
        List<ZhishuData> datas = Lists.newArrayList();
        try {
            String content = download(swsCode);
            Parser parser = new Parser();
            parser.setInputHTML(content);
            NodeList nodes = parser.parse(new TagNameFilter("table"));
            if (nodes == null || nodes.size() == 0) return datas;
            TableTag node = (TableTag) nodes.elementAt(0);
            for (TableRow row : node.getRows()) {
                ZhishuData data = parseRow(swsCode, zhishuId, row);
                if (data == null) continue;
                datas.add(data);
            }
        } catch (Exception e) {
            log.error("parse error: {}", e.getMessage());
        }
        return datas;
    }

    private static ZhishuData parseRow(String swsCode, Long zhishuId, TableRow row) {
        try {
            TableColumn[] columns = row.getColumns();
            if (columns.length < 12) return null;

            String code = getColumnText(columns[0]);
            if (!code.equals(swsCode)) {
                log.error("code ({}) is not {}", code, swsCode);
                return null;
            }

            String date = getColumnText(columns[2]);
            if (date.length() < 10) {
                log.error("date is not valid: " + date);
                return null;
            }
            date = date.substring(0, 10);
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                log.error("date is not valid: " + date);
                return null;
            }


            Double shoupan = Double.valueOf(getColumnText(columns[6]));
            Double pe = Double.valueOf(getColumnText(columns[11]));
            Double pb = Double.valueOf(getColumnText(columns[12]));

            ZhishuData data = new ZhishuData();
            data.setZhishuId(zhishuId);
            data.setDataDate(date);
            data.setPb(pb);
            data.setPe(pe);
            data.setShoupan(shoupan);
            return data;
        } catch (Exception e) {
            log.error("parse table row error: {}, message: {}", row, e.getMessage());
        }
        return null;
    }

    private static String getColumnText(TableColumn column) {
        String s = column.getStringText();
        if (s == null) return "";
        s = s.trim();
        return s.replaceAll(",", "");
    }

    public static String download(String code) throws Exception {
        String url = "http://www.swsindex.com/downloadfiles.aspx?swindexcode=" + code
                + "&type=510&columnid=8890";
        HttpGet get = new HttpGet(url);
        get.addHeader("Referer", "http://www.swsindex.com/idx0510.aspx");
        get.addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
        byte[] bytes = HttpClientUtils.execute(get, -1);
        String content = new String(bytes);
        return content;
    }

    public static void main(String[] args) throws Exception {
        parse("801001", 1L);
    }
}
