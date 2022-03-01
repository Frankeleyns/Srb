package com.frankeleyn.srb.core.controller.api;


import com.frankeleyn.common.result.R;
import com.frankeleyn.srb.core.pojo.entity.Lend;
import com.frankeleyn.srb.core.service.LendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 前端控制器
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@RestController
@RequestMapping("/api/core/lend")
public class ApiLendController {

    @Autowired
    private LendService lendService;

    @GetMapping("/getInterestCount/{investAmount}/{lendYearRate}/{period}/{returnMethod}")
    public R getInterestCount(@PathVariable("investAmount") BigDecimal investAmount, @PathVariable("lendYearRate") BigDecimal lendYearRate,
                              @PathVariable("period") Integer period, @PathVariable("returnMethod") Integer returnMethod) {

        BigDecimal interestCount = lendService.getInterestCount(investAmount, lendYearRate, period, returnMethod);
        return R.ok("interestCount", interestCount);
    }

    @GetMapping("/show/{id}")
    public R show(@PathVariable("id") Long id) {
        Map<String, Object> lendDetail = lendService.show(id);
        return R.ok("lendDetail", lendDetail);
    }

    @GetMapping("/list")
    public R getList() {
        List<Lend> list = lendService.getList();
        return R.ok("lendList", list);
    }
}

