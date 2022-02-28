package com.frankeleyn.srb.rabbitUtil.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Frankeleyn
 * @date 2022/2/28 9:45
 */
@Component
public class MqAckConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback{

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // @PostConstruct: 在构造器之后执行该方法
    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
        rabbitTemplate.setMandatory(true);
    }

    /**
     * 交换机正确触发
     * @param correlationData
     * @param ack 消息是否发送成功
     * @param cause 消息发送失败的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("消息发送确认：确认消息是否能够发送给 MQ");
    }

    /**
     * 路由错误才会触发
     * @param message
     * @param replyCode 消息投递码类似 Http 状态码
     * @param replyText 发送失败的错误提示
     * @param exchange
     * @param routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println("消息投递确认：确认消息是否从 Exchange 投递给 Queue" + replyCode + " --- " + replyText);
    }
}
