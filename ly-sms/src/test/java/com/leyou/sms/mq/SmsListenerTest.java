package com.leyou.sms.mq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsListenerTest {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void testMq(){
        HashMap<Object, Object> msg = new HashMap<>();
        msg.put("phone","18131377291");
        msg.put("code","54321");
        amqpTemplate.convertAndSend("ly.sms.exchange","sms.verify.code",msg);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
