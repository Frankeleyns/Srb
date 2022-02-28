# RabbitMQ 高级特性



## 一、消息的可靠投递



### 1. 简介

在使用 RabbitMQ 的时候，作为消息发送方希望**杜绝任何消息丢失**或者**投递失败**场景。RabbitMQ 为我们提供了**两种方式**用来**控制消息的投递可靠性模式**。

rabbitmq 整个消息投递的路径为：

producer —> rabbitmq broker —> exchange —> queue—> consumer

消息从 producer 到 exchange 发送失败则会返回一个 **confirmCallback** 。

消息从 exchange–>queue 投递失败则会返回一个 **returnCallback** 。



### 2.实现

#### ① 增加配置

在 **service-mqtest** 中增加两行配置

```properties
# 开启消息发送确认
spring.rabbitmq.publisher-return=true
# 开启消息投递确认
spring.rabbitmq.publisher-confirm-type=correlated
```

#### ② 消息回调配置类

在 **service-mq** 项目中，新建 **MqAckConfig** 类：

```java
package com.frankeleyn.srb.rabbitUtil.config;

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
```





## 二、消息的可靠消费





## 三、TTL





## 四、死信队列





## 五、延迟队列

