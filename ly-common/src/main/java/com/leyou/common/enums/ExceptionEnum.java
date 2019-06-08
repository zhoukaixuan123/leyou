package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 枚举类型   必须写分号
 * 并且具有固定实例个数的类，
 * 枚举的构造默认私有，变量也是默认私有
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {

     PRICE_CANNOT_BY_NUll(400, "价格不能为空！"),
    CATEGORY_NOT_FONO(404, "商品分类没查到"),
    BRAND_NOT_FOUND(404, "品牌没有查到"),
    BRAND_SAVE_ERROR(500, "插入数据失败"),
    UPLOAD_FILE_ERROR(500, "上传图片失败"),
    INVALID_FILE_TYPE(400, "无效的参数类型"),
    SELE_GROUP_NOT_FOND(500, "商品规则组没有查到"),
    SELE_PARAM_NOT_FOND(500, "商品规格不存在"),
    GOODS_NOT_FOUND(500, "商品不存在"),
    CREAT_CATE_SB(500, "商品保存失败"),
    SPEC_NOT_FOUNT(500,"商品规格参数不存在"),
    INVALD_USE_DATA_TYPE(400,"用户数据类型不正确"),
    INVALID_VERIFY_CODE(400,"无效的验证码"),
    INVALID_USERNAME_PASSWORD(400,"用户名或者密码错误"),
    USERNAME_OR_PASSWORD_ERROR(400,"授权中心用户密码错误"),
    USER_NOTFOUND(400,"用户未授权"),
    CART_NOT_FOUND(404,"购物车为空"),
    CREATE_ORDER_ERROR(400,"创建订单失败"),
    STOCK_NOT_FOUNT(400,"减库存失败"),
    ORDER_NOT_FOUND(400,"订单不存在"),
    DETAIL_NOT_FOUND(400,"订单详情不存在"),
    ORDERSTATUS_NOT_FOUND(400,"订单状态不存在"),
    WX_PAY_ORDER_FAIL(400,"通讯失败"),
    ORDER_STATS_ERROR(400,"订单有误"),
    QM_ERROR(400,"签名异常"),
    MINVALID_ORER_PABAM(400,"校验金额异常"),
    UPDATE_ORDER_STATUTS_ERROR(400,"更新订单失败")
    ;


    private int code;
    private String msg;

}
