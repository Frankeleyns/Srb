package com.frankeleyn.srb.sms.service;

/**
 * @author Frankeleyn
 * @date 2022/2/9 14:39
 */
public interface SmsService {
    /**
     * 发送验证码
     * @param mobile
     */
    void send(String mobile);
}
