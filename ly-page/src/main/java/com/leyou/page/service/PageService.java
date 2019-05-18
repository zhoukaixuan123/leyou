package com.leyou.page.service;

import com.leyou.item.pojo.*;
import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/5/18$
 * @description $
 */
@Service
public class PageService {

    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;

    public Map<String, Object> loadMpdel(Long spuId) {
        Map<String, Object> map = new HashMap<>();
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        //查询skus
        List<Sku> skus = spu.getSkus();
        //查询详情
        SpuDetail detail = spu.getSpuDetail();
        //查询brand
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        //查询商品分类
        List<Category> categories = categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //查询规格参数
        Long cid3 = spu.getCid3();
        List<Specification> specs = specificationClient.querySpecsByCid(cid3);
        map.put("title", spu.getTitle());
        map.put("subTitle", spu.getSubTitle());
        map.put("skus", skus);
        map.put("detail", detail);
        map.put("brand", brand);
        map.put("categories", categories);
        map.put("specs", specs);
        return map;
    }
}
