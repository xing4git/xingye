package cn.xing.xingye.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangxing on 15/12/9.
 */
@Controller
@RequestMapping("/invest")
public class InvestController {
    private static final Logger log = LoggerFactory.getLogger(InvestController.class);
    private static BigDecimal hundred = BigDecimal.valueOf(100);

    @RequestMapping(value = {"", "prediction"})
    public ModelAndView prediction(@RequestParam(required = false, value = "initMoney") BigDecimal initMoney,
                                   @RequestParam(required = false, value = "initNonMoney") BigDecimal initNonMoney,
                                   @RequestParam(required = false, value = "incomePerYear") BigDecimal incomePerYear,
                                   @RequestParam(required = false, value = "incomeNonPerYear") BigDecimal incomeNonPerYear,
                                   @RequestParam(required = false, value = "rate") BigDecimal rate,
                                   @RequestParam(required = false, value = "tongpengRate") BigDecimal tongpengRate,
                                   @RequestParam(required = false, value = "years") Integer years) {
        ModelMap model = new ModelMap();
        String viewName = "invest/prediction";

        if (initMoney == null || initNonMoney == null) return new ModelAndView(viewName, model);
        if (incomePerYear == null || incomeNonPerYear == null) return new ModelAndView(viewName, model);
        if (rate == null || years == null || tongpengRate == null) return new ModelAndView(viewName, model);
        rate = rate.add(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100));

        Map<Integer, Map<String, BigDecimal>> yearsMoney = Maps.newLinkedHashMap();
        BigDecimal lastYearTotal = initMoney.add(initNonMoney).setScale(3, RoundingMode.FLOOR);
        BigDecimal tongpengBase = hundred.add(tongpengRate).divide(hundred);
        for (int i = 1; i <= years; i++) {
            Map<String, BigDecimal> yearMoney = Maps.newHashMap();
            BigDecimal incomeTotal = initMoney.multiply(rate.pow(i));
            for (int j = 1; j <= i; j++) {
                incomeTotal = incomeTotal.add(incomePerYear.multiply(rate.pow(i - j)));
            }
            incomeTotal = incomeTotal.setScale(3, RoundingMode.FLOOR);
            BigDecimal nonIncomeTotal = initNonMoney.add(incomeNonPerYear.multiply(new BigDecimal(i)))
                    .setScale(3, RoundingMode.FLOOR);
            yearMoney.put("incomeTotal", incomeTotal);
            yearMoney.put("nonIncomeTotal", nonIncomeTotal);
            BigDecimal total = incomeTotal.add(nonIncomeTotal).setScale(3, RoundingMode.FLOOR);
            BigDecimal factTotal = total.divide(tongpengBase.pow(i), 3, RoundingMode.FLOOR);
            yearMoney.put("total", total);
            yearMoney.put("factTotal", factTotal);
            yearMoney.put("inc", total.subtract(lastYearTotal));
            lastYearTotal = total;
            BigDecimal activeTotal = initMoney.add(initNonMoney)
                    .add(incomePerYear.add(incomeNonPerYear).multiply(BigDecimal.valueOf(i)))
                    .setScale(3, RoundingMode.FLOOR);
            BigDecimal nonActiveTotal = total.subtract(activeTotal).setScale(3, RoundingMode.FLOOR);
            BigDecimal nonActiveRate = nonActiveTotal.divide(total, 4, RoundingMode.FLOOR)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(3, RoundingMode.FLOOR);
            yearMoney.put("activeTotal", activeTotal);
            yearMoney.put("nonActiveTotal", nonActiveTotal);
            yearMoney.put("nonActiveRate", nonActiveRate);
            yearsMoney.put(i, yearMoney);
        }
        model.put("yearsMoney", yearsMoney);

        return new ModelAndView(viewName, model);
    }

    @RequestMapping("dingtou")
    public ModelAndView dingtou(@RequestParam(required = false, value = "initPrice") BigDecimal initPrice,
                                @RequestParam(required = false, value = "initCost") BigDecimal initCost,
                                @RequestParam(required = false, value = "maxPrice") BigDecimal maxPrice,
                                @RequestParam(required = false, value = "priceChangeRate") BigDecimal priceChangeRate,
                                @RequestParam(required = false, value = "costChangeRate") BigDecimal costChangeRate) {
        ModelMap model = new ModelMap();
        String viewName = "invest/dingtou";

        if (initPrice == null || initCost == null) return new ModelAndView(viewName, model);
        if (priceChangeRate == null || costChangeRate == null) return new ModelAndView(viewName, model);
        if (maxPrice == null) {
            maxPrice = initPrice.multiply(BigDecimal.valueOf(2));
        }

        List<Map<String, Object>> plans = Lists.newArrayList();
        BigDecimal hundred = BigDecimal.valueOf(100);
        // 价格下降
        for (int i = 0; true; i++) {
            BigDecimal currentPrice = initPrice.multiply(hundred.subtract(priceChangeRate).divide(hundred).pow(i))
                    .setScale(3, RoundingMode.FLOOR);
            if (currentPrice.compareTo(BigDecimal.ZERO) <= 0) break;
            BigDecimal currentCost = initCost.multiply(hundred.add(costChangeRate).divide(hundred).pow(i))
                    .setScale(3, RoundingMode.FLOOR);
            if (currentCost.intValue() > 30000) break;
            int mount = currentCost.divide(currentPrice, 3, RoundingMode.FLOOR).intValue() / 100 * 100;
            plans.add(genPlan(currentPrice, mount));
        }
        // 价格上升
        for (int i = 1; true; i++) {
            BigDecimal currentPrice = initPrice.multiply(hundred.add(priceChangeRate).divide(hundred).pow(i))
                    .setScale(3, RoundingMode.FLOOR);
            if (currentPrice.compareTo(maxPrice) >= 0) break;
            BigDecimal currentCost = initCost.multiply(hundred.subtract(costChangeRate).divide(hundred).pow(i))
                    .setScale(3, RoundingMode.FLOOR);
            if (currentCost.compareTo(BigDecimal.ZERO) < 0) break;
            int mount = currentCost.divide(currentPrice, 3, RoundingMode.FLOOR).intValue() / 100 * 100;
            plans.add(0, genPlan(currentPrice, mount));
        }
        model.put("plans", plans);

        return new ModelAndView(viewName, model);
    }

    private Map<String, Object> genPlan(BigDecimal currentPrice, Integer mount) {
        BigDecimal cost = currentPrice.multiply(BigDecimal.valueOf(mount))
                .setScale(3, RoundingMode.FLOOR);
        Map<String, Object> plan = Maps.newHashMap();
        plan.put("currentPrice", currentPrice);
        plan.put("mount", mount);
        plan.put("cost", cost);
        return plan;
    }
}
