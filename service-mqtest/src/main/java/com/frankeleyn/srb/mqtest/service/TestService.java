package com.frankeleyn.srb.mqtest.service;

/**
 * @author Frankeleyn
 * @date 2022/2/26 16:45
 */
public interface TestService {

    /**
     * 发送充值消息
     */
    void recharge();

    /**
     * 测试确认消息
     */
    void confirm();

    /**
     * 测试过期时间
     */
    void testTTL();

    void testDelay();
}
