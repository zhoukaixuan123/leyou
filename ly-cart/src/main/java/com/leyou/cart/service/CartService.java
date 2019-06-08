package com.leyou.cart.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.cart.config.interceptor.UserInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundGeoOperations;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author zhoukx
 * @date 2019/6/8$
 * @description $
 */
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "cart:user:id";

    /**
     * redis  map 结构
     *
     * @param cart
     */
    public void addCart(Cart cart) {
        //获取登陆的用户
        UserInfo userInfo = UserInterceptor.getUser();
        //key
        String key = KEY_PREFIX + userInfo.getId();
        //hashKey
        String hashKey = cart.getSkuId().toString();
        //记录num
        Integer num = cart.getNum();
        //判断当前购物车中是否存在
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
        //当前购物车中是否存在
        if (operation.hasKey(hashKey)) {
            //是修改
            //存在修改数量
            String json = operation.get(hashKey).toString();
            cart = JsonUtils.parse(json, Cart.class);
            cart.setNum(cart.getNum() + cart.getNum());

        }
        //写回redis
        operation.put(hashKey, JsonUtils.serialize(cart));


    }

    /***
    * @Description: 查询购物车信息
    * @Param: []
    * @return: List<Cart>
    * @Author: zhoukx
    * @Date: 2019/6/8
    */
    public List<Cart> quertCartList() {
        //获取登陆的用户
        UserInfo userInfo = UserInterceptor.getUser();

        //key
        String key = KEY_PREFIX + userInfo.getId();
       if(!redisTemplate.hasKey(key)){
           //key不存在
           throw  new LyException(ExceptionEnum.CART_NOT_FOUND);
       }
        //判断当前购物车中是否存在
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
       // stream  转换成流   map中的 一个值 转换成 Cart实体列     在把这个整个 转换成list
        List<Cart> collect = operation.values().stream().map(o -> JsonUtils.parse(o.toString(), Cart.class)).collect(Collectors.toList());
        return  collect;
    }

    public void updateNum(Long skuId, Integer num) {
        //获取登陆的用户
        UserInfo userInfo = UserInterceptor.getUser();
        //key
        String key = KEY_PREFIX + userInfo.getId();
        //hashkey
        String hashKey = skuId.toString();

        if(!redisTemplate.hasKey(key)){
            //key不存在
            throw  new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
        //判断当前购物车中是否存在
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
       Cart cart =  JsonUtils.parse(JsonUtils.serialize(operation.get(skuId.toString())),Cart.class);
       cart.setNum(num);
       //写回redis
        operation.put(hashKey,JsonUtils.serialize(cart));

    }

    public void deleteCart(Long skuId) {
        //获取登陆的用户
        UserInfo userInfo = UserInterceptor.getUser();
        //key
        String key = KEY_PREFIX + userInfo.getId();
        //hashkey
        String hashKey = skuId.toString();

        if(!redisTemplate.hasKey(key)){
            //key不存在
            throw  new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
        //删除商品
        redisTemplate.opsForHash().delete(key,skuId.toString());
    }
}
