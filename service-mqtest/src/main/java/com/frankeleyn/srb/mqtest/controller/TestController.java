package com.frankeleyn.srb.mqtest.controller;

import com.frankeleyn.common.result.R;
import com.frankeleyn.srb.mqtest.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Frankeleyn
 * @date 2022/2/26 16:44
 */
@RestController
public class TestController {

    @Autowired
    TestService testService;

    @GetMapping("/recharge")
    public R recharge() {
        testService.recharge();
        return R.ok();
    }
}
