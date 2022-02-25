package com.frankeleyn.srb.core.controller.api;


import com.frankeleyn.common.result.R;
import com.frankeleyn.srb.core.pojo.entity.Lend;
import com.frankeleyn.srb.core.service.LendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/list")
    public R getList() {
        List<Lend> list = lendService.getList();
        return R.ok("lendList", list);
    }
}

