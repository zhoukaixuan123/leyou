package com.leyou.order.enums;

/**
 * 功能描述
 *
 * @author zhoukx
 * @date 2019/6/9$
 * @description 返回订单状态$
 */
public enum  PayState {
   NOT_PAY(0),SUCCESS(1),FAIL(2);

   PayState(int value){ this.value = value;}

   int value;
   public int  getValue(){
       return  value;
   }

}
