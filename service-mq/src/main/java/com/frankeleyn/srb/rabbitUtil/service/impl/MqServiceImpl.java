package com.frankeleyn.srb.rabbitUtil.service.impl;

import com.frankeleyn.srb.rabbitUtil.service.MqService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Frankeleyn
 * @date 2022/2/26 16:27
 */
@Service
public class MqServiceImpl implements MqService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public boolean sendMessage(String exchange, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        return true;
    }
}
