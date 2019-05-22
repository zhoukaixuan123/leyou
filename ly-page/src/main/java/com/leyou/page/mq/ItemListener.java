package com.leyou.page.mq;

import com.leyou.page.service.PageService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



/**
 * 功能描述
 *
 * @author zhoukx
 * @date 2019/5/22$
 * @description mq消费者$
 */
@Component
public class ItemListener {
     @Autowired
    private PageService pageService;


    /**
     * 创建静态页
     *
     * @param id
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "page.item.insert.queue", durable = "true"),
            exchange = @Exchange(
                    value = "ly.item.exchange",
                    type = ExchangeTypes.TOPIC),
            key = {"item.insert", "item.update"}))
    public  void ListenerInsertOrUpdate(Long spuId){
        if(spuId == null){
            return;
        }
        //静态页创建
        pageService.createHtml(spuId);
    }

    /**
     * 删除静态页
     *
     * @param id
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "page.item.delete.queue", durable = "true"),
            exchange = @Exchange(
                    value = "ly.item.exchange",
                    type = ExchangeTypes.TOPIC),
            key = {"item.delete"}))
    public  void ListenerDelete(Long spuId){
        if(spuId == null){
            return;
        }
        //静态页删除
        pageService.deleteHtml(spuId);
    }
}
