package com.leyou.item.api;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/5/5$
 * @description 被调用服务$
 */
public interface GoodsApi {

    /**
     * @Description: 查询SpuDetail接口
     * @Param: [id]
     * @return: org.springframework.http.ResponseEntity<com.leyou.item.pojo.SpuDetail>
     * @Author: zhoukx
     * @Date: 2019/5/2
     */
    @GetMapping("/spu/detail/{id}")
    SpuDetail querySpuDetailById(@PathVariable("id") Long id);

    /**
     * @Description: 查询sku
     * @Param: [id]
     * @return: org.springframework.http.ResponseEntity<List   <   Sku>>
     * @Author: zhoukx
     * @Date: 2019/5/2
     */
    @GetMapping("sku/list")
    List<Sku> querySkuBySpuId(@RequestParam("id") Long id);

    /**
     * 分页查询SPU
     *
     * @param page
     * @param rows
     * @param key
     * @return
     */
    @GetMapping("/spu/page")
    PageResult<Spu> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "key", required = false) String key);

    /**
     * 根据id查询spu
     *
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
  Spu querySpuById(@PathVariable("id") Long id);
}


