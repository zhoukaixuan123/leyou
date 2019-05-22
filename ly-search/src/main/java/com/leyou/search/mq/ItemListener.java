package com.leyou.search.mq;

import com.leyou.search.service.SearchService;
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
    private SearchService searchService;


    /**
     * 处理insert和update的消息
     *
     * @param id
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "search.item.insert.queue", durable = "true"),
            exchange = @Exchange(
                    value = "ly.item.exchange",
                    type = ExchangeTypes.TOPIC),
            key = {"item.insert", "item.update"}))
    public  void ListenerInsertOrUpdate(Long spuId){
        if(spuId == null){
            return;
        }
        //处理消息，对索引库新增或修改
        searchService.createOrUpdate(spuId);
    }

    /**
     * 删除索引
     *
     * @param id
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "search.item.delete.queue", durable = "true"),
            exchange = @Exchange(
                    value = "ly.item.exchange",
                    type = ExchangeTypes.TOPIC),
            key = {"item.delete"}))
    public  void ListenerDelete(Long spuId){
        if(spuId == null){
            return;
        }
        //处理消息，对索引库新增或修改
        searchService.deleteIndex(spuId);
    }
}
