package com.frankeleyn.srb.mqtest.service.impl;

import com.frankeleyn.srb.mqtest.service.TestService;
import com.frankeleyn.srb.rabbitUtil.service.MqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Frankeleyn
 * @date 2022/2/26 16:46
 */
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    MqService mqService;

    @Override
    public void recharge() {
        System.out.println("用户充值成功");
        mqService.sendMessage("exchange.test", "routing.test", "充值成功");
        System.out.println("向 mq 发送充值成功消息");
    }

    @Override
    public void confirm() {
        System.out.println("发送消息队列");
        mqService.sendMessage("exchange.confirm", "routing.confirm", "消息确认测试");

    }
}
