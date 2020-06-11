package cn.xing.xingye.touzi.model;

import cn.xing.xingye.touzi.utils.XingConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by zhangxing on 15/12/21.
 * 资产
 */
public class Asset extends BaseModel {
    private static final Logger log = LoggerFactory.getLogger(Asset.class);

    private Long groupId; // 分组ID
    private BigDecimal income; // 实际投入
    private BigDecimal real; // 市值

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        if (income != null) {
            income.setScale(XingConst.BIGDECIMAL_SCALE, RoundingMode.HALF_UP);
        }
        this.income = income;
    }

    public BigDecimal getReal() {
        return real;
    }

    public void setReal(BigDecimal real) {
        if (real != null) {
            real.setScale(XingConst.BIGDECIMAL_SCALE, RoundingMode.HALF_UP);
        }
        this.real = real;
    }
}
