package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 枚举类型   必须写分号
 *   并且具有固定实例个数的类，
 *   枚举的构造默认私有，变量也是默认私有
 *
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {

   PRICE_CANNOT_BY_NUll(400,"价格不能为空！")
    ;

    private int code;
    private String msg;

}
