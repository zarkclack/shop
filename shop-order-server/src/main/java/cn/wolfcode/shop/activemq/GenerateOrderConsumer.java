package cn.wolfcode.shop.activemq;

import cn.wolfcode.shop.service.IOrderService;
import cn.wolfcode.shop.vo.OrderVo;
import com.alibaba.fastjson.JSON;
import org.apache.activemq.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@Component
public class GenerateOrderConsumer {

    @Autowired
    private IOrderService orderService;

    @JmsListener(destination = "generateOrder",containerFactory = "jmsListenerContainerQueue")
    //@JmsListener(destination = "generateOrder",containerFactory = "jmsListenerContainerTopic")
    public void listenerMessage(Message message) throws JMSException {
        TextMessage textMessage = (TextMessage) message;
        System.out.println(textMessage.getText());
        OrderVo orderVo = JSON.parseObject(textMessage.getText(), OrderVo.class);
        orderService.generateOrder(orderVo);
    }
}
