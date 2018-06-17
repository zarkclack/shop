package cn.wolfcode.shop.activemq;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Destination;

@Component
public class GenerateOrderProducer {

    /**
     *
     *注入spirng帮我们封装好的JMS模板类
     */
    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * 确定消息发送到哪个地点
     */
    private Destination queue = new ActiveMQQueue("generateOrder");

    private Destination topic = new ActiveMQTopic("generateOrder");

    /**
     * 发送生成订单消息
     */
    public void sendMessage(String content){
        //实用JMS模板类发送消息
        jmsTemplate.convertAndSend(queue,content);
    }

}
