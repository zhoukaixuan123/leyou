package com.leyou.order.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.common.dto.CartDto;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.pojo.Sku;
import com.leyou.order.client.AddressClient;
import com.leyou.order.client.GoodsClient;
import com.leyou.order.config.IdWorkerConfig;
import com.leyou.order.config.interceptor.UserInterceptor;
import com.leyou.order.dto.AddressDTO;
import com.leyou.order.dto.OrderDto;
import com.leyou.order.dto.OrderStatusEnum;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author zhoukx
 * @date 2019/6/8$
 * @description $
 */
@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private IdWorker  idWorker;

    @Autowired
     private GoodsClient goodsClient;


    @Transactional
    public Long createOrder(OrderDto orderDto) {
        //组织订单数据
        Order order = new Order();
        //订单编号
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);
        order.setCreateTime(new Date());
        order.setPaymentType(orderDto.getPaymentType());

        // 用户信息
        UserInfo user = UserInterceptor.getUser();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);
        //收货人地址信息
        AddressDTO addr = AddressClient.findById(orderDto.getAddressId());
        order.setReceiver(addr.getName());
        order.setReceiverAddress(addr.getAddress());
        order.setReceiverCity(addr.getCity());
        order.setReceiverDistrict(addr.getDistrict());
        order.setReceiverMobile(addr.getPhone());
        order.setReceiverState(addr.getState());
        order.setReceiverZip(addr.getZipCode());
        //金额相关信息
        //把cartDato转为一个map  key是 sku的id ，值是num
        Map<Long, Integer> numMap = orderDto.getCarts().stream().collect(Collectors.toMap(CartDto::getSkuId, CartDto::getNum));
       //获取所有的sku的id
        Set<Long> ids = numMap.keySet();
        //新增订单
        //根据id查询skus
        List<Sku> skus = goodsClient.querySkuByIds(new ArrayList<>(ids));
       Long  totalPay = 0L;
       //准备OrderDatil的集合
        List<OrderDetail> details = new ArrayList<>();
        for (Sku sku : skus) {
            totalPay+= sku.getPrice()*numMap.get(sku.getId());
            //封装 OrderDetial
            OrderDetail detail = new OrderDetail();
            detail.setImage(StringUtils.substringBefore(sku.getImages(),","));
            detail.setNum(numMap.get(sku.getId()));
            detail.setOrderId(orderId);
            detail.setOwnSpec(sku.getOwnSpec());
            detail.setPrice(sku.getPrice());
            detail.setSkuId(sku.getId());
            detail.setTitle(sku.getTitle());
            details.add(detail);
        }
        order.setTotalPay(totalPay);
        //实付金额
        order.setActualPay(totalPay+order.getPostFee()-0);
        //order  写入数据库
       int count = orderMapper.insertSelective(order);
       if(count != 1 ){
           log.error("【创建订单失败】，orderId：{}"+orderId);
           throw  new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
       }
        //新增订单的详情
        count = orderDetailMapper.insertList(details);
        if(count < 1 ){
            log.error("【创建订单失败】，orderId：{}"+orderId);
            throw  new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }
        //新增订单的状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCreateTime(new Date());
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusEnum.DELIVERED.value());
      count =  orderStatusMapper.insertSelective(orderStatus);
        if(count < 1 ){
            log.error("【创建订单失败】，orderId：{}"+orderId);
            throw  new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }
        //减库存
        List<CartDto> cartDTOS = orderDto.getCarts();
        goodsClient.decreaseStock(cartDTOS);
        return  orderId;
    }
}