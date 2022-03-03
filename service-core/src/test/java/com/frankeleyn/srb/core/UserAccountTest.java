package com.frankeleyn.srb.core;

import com.frankeleyn.srb.core.service.UserAccountService;
import com.frankeleyn.srb.core.service.UserInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 * @author Frankeleyn
 * @date 2022/3/2 10:53
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserAccountTest {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UserInfoService userInfoService;

    @Test
    public void test1() {
        BigDecimal serviceRate = new BigDecimal("0.05");
        BigDecimal rate = serviceRate.divide(new BigDecimal("12"), 8, BigDecimal.ROUND_HALF_DOWN);
        System.out.println(rate);
    }

}
