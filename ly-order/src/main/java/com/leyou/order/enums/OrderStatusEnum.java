package com.leyou.order.enums;

/**
 * 功能描述
 *
 * @author zhoukx
 * @date 2019/6/8$
 * @description 枚举方便看类型$
 */
public enum  OrderStatusEnum {
    UNPAY(1,"未付款"),
    PAYED(2,"已付款"),
    DELIVERED(3,"已付款未发货"),
    SUCCESS(4,"已确认，交易成功，未评价"),
    CLOSE(5,"交易失败，已关闭"),
    RATED(6,"已评价");
    private  int  code;
    private String desc;



     OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public int value(){
        return this.code;
    }
}
