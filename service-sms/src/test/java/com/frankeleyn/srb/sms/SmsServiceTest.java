package com.frankeleyn.srb.sms;

import com.frankeleyn.srb.sms.controller.api.ApiSmsController;
import com.frankeleyn.srb.sms.utils.SmsProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Frankeleyn
 * @date 2022/2/9 11:50
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsServiceTest {

    @Test
    public void testProperties01() {
        System.out.println(SmsProperties.KEY_ID);
    }

    @Autowired
    ApiSmsController controller;

    @Test
    public void testCheckPhone() {
        controller.send("1314444444");
    }
}
