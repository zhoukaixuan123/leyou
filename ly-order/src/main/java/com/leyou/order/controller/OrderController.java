package com.leyou.order.controller;

import com.leyou.order.dto.OrderDto;
import com.leyou.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能描述
 *
 * @author zhoukx
 * @date 2019/6/8$
 * @description $
 */
@RestController
@RequestMapping
public class OrderController {

    @Autowired
    private OrderService orderService;

    /*** 
    * @Description: 创建订单
    * @Param: [orderDto] 
    * @return: org.springframework.http.ResponseEntity<java.lang.Long> 
    * @Author: zhoukx
    * @Date: 2019/6/8 
    */ 
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderDto orderDto ){
        //创建订单

        return  ResponseEntity.ok(orderService.createOrder(orderDto));
    }
}
