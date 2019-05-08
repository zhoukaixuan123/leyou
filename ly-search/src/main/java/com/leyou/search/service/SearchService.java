package com.leyou.search.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/5/5$
 * @description 把数据导入到索引库$
 */
@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private BrandClient brandClient;

    private ObjectMapper mapper = new ObjectMapper();

    public Goods buildGoods() throws IOException {
        return buildGoods();
    }

    public Goods buildGoods(Spu spu) throws IOException {
        Goods goods = new Goods();


        // 查询商品分类名称
        List<Category> categories = this.categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
       if(CollectionUtils.isEmpty(categories)){
           throw  new LyException(ExceptionEnum.CATEGORY_NOT_FONO);
       }
       //转换成名称的 list
       List<String> names =  categories.stream().map(Category::getName).collect(Collectors.toList());

       //查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        if(brand ==null){
            throw  new LyException(ExceptionEnum.CATEGORY_NOT_FONO);
        }
        // 查询sku
        List<Sku> skusList = this.goodsClient.querySkuBySpuId(spu.getId());
        if(CollectionUtils.isEmpty(skusList)){
            throw  new LyException(ExceptionEnum.CATEGORY_NOT_FONO);
        }
        //价格集合
        Set<Long> prices = new HashSet<>();
        // 处理sku，仅封装id、价格、标题、图片，并获得价格集合
        List<Map<String, Object>> skuList = new ArrayList<>();
        for(Sku sku:skusList){
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            skuList.add(skuMap);
            prices.add(sku.getPrice());
        };

        //拿价格
        Set<Long>  priceList= skusList.stream().map(Sku::getPrice).collect(Collectors.toSet());

        // 查询详情
        SpuDetail spuDetail = this.goodsClient.querySpuDetailById(spu.getId());
        // 查询规格参数
        List<Specparam> params = specificationClient.queryparamByList(null, spu.getCid3(), "true");
        if(CollectionUtils.isEmpty(params)){
            return goods;
        }
        //错在这里
        //获取通用规格参数
        Map<String, Object> genericSpecs = mapper.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {
        });
        //获取特有的规格参数
        Map<String, Object> specialSpecs = mapper.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<String>>>() {
        });
         //获取可搜索的规格参数
        Map<String, Object> specs = new HashMap<>();
        for (Specparam param : params) {
             //规格名称
            String key = param.getName();
            Object value = "";
            //判断是否是通用的规格
            if(param.getGeneric()){
               value = genericSpecs.get(param.getId()+"");
//                //判断是否是数值类型
                if(param.getNumeric()){
                    //处理段
                  value = chooseSegment(value.toString(),param);
                }
            }else {
                value = specialSpecs.get(param.getId());

            }
            specs.put(key,value);
        }



        goods.setId(spu.getId());
        goods.setSubTitle(spu.getSubTitle());
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setAll(spu.getTitle() + " " + StringUtils.join(names, " "));//搜索字段，包含标题，分类等
        goods.setPrice(priceList);      //所有的sku的价格集合
        goods.setSkus(mapper.writeValueAsString(skuList));  //所有的sku的集合的json
        goods.setSpecs(specs);  //所有的可搜索的规格参数
        return goods;
    }



    private String chooseSegment(String value, Specparam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }
}