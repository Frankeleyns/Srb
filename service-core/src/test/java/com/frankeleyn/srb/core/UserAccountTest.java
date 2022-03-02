package com.frankeleyn.srb.core;

import com.frankeleyn.srb.core.service.UserAccountService;
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

    @Test
    public void test1() {
        BigDecimal a = new BigDecimal("5");
        BigDecimal b = new BigDecimal("4");
        BigDecimal add = a.add(b.negate());
        System.out.println(add);

    }

}
