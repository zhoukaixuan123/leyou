package com.leyou.page.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;


/*
 *功能描述
 * @author zhoukx
 * @date 2019/5/5$
 * @description 商品服务调用$
 */
@FeignClient("item-service")
public interface CategoryClient  extends CategoryApi {


}
