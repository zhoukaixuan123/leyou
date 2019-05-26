package com.leyou.sms.mq;


import com.alibaba.fastjson.JSON;
import com.aliyuncs.exceptions.ClientException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utils.SmsUtils;

import org.json.JSONObject;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


import java.util.Map;


/**
 * 功能描述
 *
 * @author zhoukx
 * @date 2019/5/22$
 * @description mq消费者$
 */
@Component
public class SmsListener {


    @Autowired
    private SmsUtils smsUtils;

    @Autowired
    private SmsProperties pop;
    @Autowired
    private SmsProperties smsProperties;

    /**
     * 发送短信验证码
     *
     * @param
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "sms.verify.code.queue", durable = "true"),
            exchange = @Exchange(
                    value = "ly.sms.exchange",
                    type = ExchangeTypes.TOPIC),
            key = {"sms.verify.code"}))
    public void ListenerInsertOrUpdate(Map<String, String> msg) {
        if (CollectionUtils.isEmpty(msg)) {
            return;
        }
        String phone = msg.remove("phone");
        if (StringUtils.isEmpty(phone)) {
            return;
        }

        smsUtils.sendSms(phone,smsProperties.getSignName(),smsProperties.getVerifyCodeTemplate(), JSON.toJSONString(msg));
        System.out.println("短信发送成功");
    }


}
