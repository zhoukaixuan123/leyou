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
    USER_NOTFOUND(400,"用户未授权")
    ;



    private int code;
    private String msg;

}
