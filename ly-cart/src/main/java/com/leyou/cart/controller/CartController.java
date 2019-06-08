package com.leyou.cart.controller;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 功能描述
 *
 * @author zhoukx
 * @date 2019/6/8$
 * @description 购物车$
 */
@RestController
public class CartController {


    @Autowired
    private CartService cartService;
    /*** 
    * @Description: 新增购物车 
    * @Param: [cart] 
    * @return: org.springframework.http.ResponseEntity<java.lang.Void> 
    * @Author: zhoukx
    * @Date: 2019/6/8 
    */ 
   @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
       cartService.addCart(cart);
       return ResponseEntity.status(HttpStatus.CREATED).build();
   }


    /**
     * 查询购物车中的数量
     * @return
     */
   @GetMapping
   public ResponseEntity<List<Cart>> quertCartList(){
       return  ResponseEntity.ok(cartService.quertCartList());
   }

    /**
     * 修改购物车中的数量
     * @param skuId
     * @param num
     * @return
     */
    @PutMapping
    public ResponseEntity<Void>  updateCartNum(@RequestParam("skuId") Long skuId ,@RequestParam("num") Integer num){
        cartService.updateNum(skuId,num);
        return  ResponseEntity.ok().build();
   }

   /*** 
   * @Description: 删除商品
   * @Param: [] 
   * @return: org.springframework.http.ResponseEntity<java.lang.Void> 
   * @Author: zhoukx
   * @Date: 2019/6/8 
   */ 
   @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") Long skuId){
       cartService.deleteCart(skuId);
       return ResponseEntity.ok().build();
   }

}
