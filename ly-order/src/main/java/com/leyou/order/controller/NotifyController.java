package com.leyou.order.controller;

import com.leyou.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述
 *
 * @author zhoukx
 * @date 2019/6/9$
 * @description 内网穿透   微信回调$
 */
@Slf4j
@RestController
@RequestMapping("notify")
public class NotifyController {

    @Autowired
    private OrderService orderService;

    /*** 
    * @Description: 微信支付成功回调
    * @Param: [result] 
    * @return: java.lang.String 
    * @Author: zhoukx
    * @Date: 2019/6/9 
    */ 
   @PostMapping(value = "pay",produces = "application/xml")
    public   Map<String,String>   hello(@RequestBody Map<String,String> result){
       //处理回调
       orderService.handLeNotify(result);

       //返回成功
       Map<String,String> msg = new HashMap<>();
       msg.put("return_code","SUCCESS");
       msg.put("return_msg","OK");
       log.info("微信回调");
       return  msg;
   }
}
