package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


/***
* @Description: 商品详情表
* @Param:  
* @return:  
* @Author: zhoukx
* @Date: 2019/4/24 
*/ 
@Table(name="tb_spu_detail")
@Data
public class SpuDetail {
    @Id
    private Long spuId;// 对应的SPU的id
    private String description;// 商品描述
    private String specTemplate;// 商品特殊规格的名称及可选值模板

    private String specifications;// 商品的全局规格属性
    private String packingList;// 包装清单
    private String afterService;// 售后服务
    // 省略getter和setter


    @Column(name = "special_spec")
    private String  specialSpec;

    @Column(name = "generic_spec")
    private String  genericSpec;
}