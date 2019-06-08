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
import com.leyou.order.enums.PayState;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.order.utils.PayHelper;
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

    @Autowired
    private PayHelper payHelper;
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
        orderStatus.setStatus(OrderStatusEnum.INIT.value());
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

    public Order querOrderById(Long id) {
        Order order = orderMapper.selectByPrimaryKey(id);
        if(order == null){
            throw  new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        //查询订单详情
        OrderDetail detail = new OrderDetail();
        detail.setOrderId(id);
        List<OrderDetail> details = orderDetailMapper.select(detail);
        if(details == null){
            throw  new LyException(ExceptionEnum.DETAIL_NOT_FOUND);

        } //查询订单状态
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(id);
        if(orderStatus == null){
            throw  new LyException(ExceptionEnum.ORDERSTATUS_NOT_FOUND);

        }
        order.setOrderStatus(orderStatus);
        order.setOrderDetails(details);
        return  order;
    }

    public String createOrderPayUrl(Long orderId) {
        //查询订单
        Order order = querOrderById(orderId);
        //判断订单的状态
        Integer status = order.getOrderStatus().getStatus();
        if(status != com.leyou.order.enums.OrderStatusEnum.UNPAY.value()){
             throw  new LyException(ExceptionEnum.ORDER_STATS_ERROR);
        }
        //支付金额
        Long actualPay = /*order.getActualPay()*/1L;
        //商品描述
        String desc = "";
        try {

            OrderDetail detail = order.getOrderDetails().get(0);
             desc = detail.getTitle();
        }catch (Exception ee){

        }
        //调用微信付款工具类
        return payHelper.createOrder(orderId, actualPay, desc);

    }

    public void handLeNotify(Map<String, String> result) {
      // 数据校验
        payHelper.isSuccess(result);
        //签名校验
        payHelper.isValiDsign(result);
        //校验金额
        String totalFfeeStr = result.get("total_fee");
        String tradeNo = result.get("out_trade_no");
        if(StringUtils.isEmpty(totalFfeeStr) ||StringUtils.isEmpty(tradeNo)){
           throw  new LyException(ExceptionEnum.MINVALID_ORER_PABAM);
        }
        Long totalFee = Long.valueOf(totalFfeeStr);
        //获取订单金额
        Long orderId = Long.valueOf(totalFee);
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(totalFee != /*order.getActualPay()*/ 1){
            //金额不相等
            throw  new LyException(ExceptionEnum.MINVALID_ORER_PABAM);
        }
        //修改状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(Long.valueOf(tradeNo));
        orderStatus.setStatus(com.leyou.order.enums.OrderStatusEnum.PAYED.value());
        orderStatus.setPaymentTime(new Date());
        int count = orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
        log.info("[修改订单状态]"+count);
        if(count != 1){
            throw  new LyException(ExceptionEnum.UPDATE_ORDER_STATUTS_ERROR);

        }
        log.info("[订单状态执行完成]"+count);
    }

    public Integer quertOrderState(Long orderId) {
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
        Integer status = orderStatus.getStatus();
        if(status != com.leyou.order.enums.OrderStatusEnum.UNPAY.value()){
           //如果已支付真的已支付了
            return 1;
        }
        //如果未支付  不一定真的位置  可能消息还没通知到
        return payHelper.queryPayState(orderId);

    }
}