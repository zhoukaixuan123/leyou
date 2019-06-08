package com.leyou.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能描述
 *
 * @author zhoukx
 * @date 2019/6/8$
 * @description 创建商品的Dto$
 */

@AllArgsConstructor
@NoArgsConstructor
public class    CartDto {
    /**
     * 商品的skuid
     */
    private Long skuId;
    /***
    * @Description: 购买商品的数量
    * @Param:
    * @return:
    * @Author: zhoukx
    * @Date: 2019/6/8
    */
    private Integer num;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
