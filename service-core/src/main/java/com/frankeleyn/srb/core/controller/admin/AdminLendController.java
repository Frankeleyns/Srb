package com.frankeleyn.srb.core.controller.admin;


import com.frankeleyn.common.result.R;
import com.frankeleyn.srb.core.pojo.entity.Lend;
import com.frankeleyn.srb.core.service.LendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/admin/core/lend")
public class AdminLendController {

    @Autowired
    private LendService lendService;

    @GetMapping("/show/{id}")
    public R show(@PathVariable("id") Long id) {
        Map<String, Object> lendDetail = lendService.show(id);
        return R.ok("lendDetail", lendDetail);
    }

    @GetMapping("/list")
    public R getList() {
        List<Lend> lendList = lendService.getList();
        return R.ok("list", lendList);
    }
}

