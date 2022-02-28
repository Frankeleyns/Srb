package com.frankeleyn.srb.rabbitUtil.service;

/**
 * @author Frankeleyn
 * @date 2022/2/26 16:26
 */
public interface MqService {

    /**
     * 发送消息
     * @param exchange 交换机
     * @param routingKey 路由
     * @param message 消息
     */
    boolean sendMessage(String exchange, String routingKey, Object message);

    /**
     * 发送带存活时间的 Message
     * @param exchange
     * @param routingKey
     * @param message
     */
    void sendTTLMessage(String exchange, String routingKey, String message, Long timeMill);
}
