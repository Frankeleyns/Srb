package com.frankeleyn.srb.mqtest.receiver;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author Frankeleyn
 * @date 2022/2/26 16:52
 */
@Component
public class ConsumerTest {

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = "exchange.test"),
            key = {"routing.test"},
            value = @Queue(value = "queue.test", durable = "true")
    ))
    public void receive(Message message) {
        System.out.println("消费 test 队列");
        System.out.println(new String(message.getBody()));
    }

}
