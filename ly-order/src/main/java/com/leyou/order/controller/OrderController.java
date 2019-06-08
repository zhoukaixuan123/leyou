package com.leyou.order.controller;

import com.leyou.order.dto.OrderDto;
import com.leyou.order.pojo.Order;
import com.leyou.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 功能描述
 *
 * @author zhoukx
 * @date 2019/6/8$
 * @description $
 */
@RestController
@RequestMapping("order")
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

    /*** 
    * @Description: 查询订单信息
    * @Param: [id] 
    * @return: org.springframework.http.ResponseEntity<com.leyou.order.pojo.Order> 
    * @Author: zhoukx
    * @Date: 2019/6/9 
    */ 
    @GetMapping("{id}")
    public  ResponseEntity<Order> querOrderById(@PathVariable("id") Long id){
        return ResponseEntity.ok(orderService.querOrderById(id));
    }
    
    /*** 
    * @Description: 微信接口调用
    * @Param: [orderId] 
    * @return: org.springframework.http.ResponseEntity<java.lang.String> 
    * @Author: zhoukx
    * @Date: 2019/6/9 
    */ 
    @GetMapping("url/{id}")
    public ResponseEntity<String> createPayUrl(@PathVariable("id") Long orderId){
        String orderPayUrl = orderService.createOrderPayUrl(orderId);
        return  ResponseEntity.ok(orderPayUrl);
    }

    /***
    * @Description: 查询订单的状态
    * @Param: [orderId]
    * @return: org.springframework.http.ResponseEntity<java.lang.Integer>
    * @Author: zhoukx
    * @Date: 2019/6/9
    */
    @GetMapping("/state/{id}")
    public ResponseEntity<Integer> quertOrderState(@PathVariable("id") Long orderId){
        return  ResponseEntity.ok(orderService.quertOrderState(orderId));
    }

}
