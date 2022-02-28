package com.frankeleyn.srb.sms.receiver;

import com.frankeleyn.srb.dto.SmsDTO;
import com.frankeleyn.srb.rabbitUtil.constant.MQConst;
import com.frankeleyn.srb.sms.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class SmsReceiver {

    @Resource
    private SmsService smsService;

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = MQConst.EXCHANGE_TOPIC_SMS),
            value = @Queue(value = MQConst.QUEUE_SMS_ITEM, durable = "true"),
            key = {MQConst.ROUTING_SMS_ITEM}
    ))
    public void send(SmsDTO smsDTO) {
        System.out.println("消费消息，发送充值成功短信");
    }
}