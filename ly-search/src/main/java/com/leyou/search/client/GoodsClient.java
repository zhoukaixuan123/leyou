package com.leyou.search.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;


/*** 
* @Description: 品牌调用
* @Param:  
* @return:  
* @Author: zhoukx
* @Date: 2019/5/5 
*/ 
@FeignClient(value = "item-service")
public interface GoodsClient  extends GoodsApi {


}
