package com.leyou.search.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
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
@Slf4j
@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private BrandClient brandClient;

    private ObjectMapper mapper = new ObjectMapper();



    @Autowired
    private GoodsRepository goodsRepository;


    public Goods buildGoods() throws IOException {
        return buildGoods();
    }

    public Goods buildGoods(Spu spu) throws IOException {
        Goods goods = new Goods();

        // 查询商品分类名称
        List<Category> categories = this.categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        if (CollectionUtils.isEmpty(categories)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FONO);
        }
        //转换成名称的 list
        List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());

        //查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        if (brand == null) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FONO);
        }
        // 查询sku
        List<Sku> skusList = this.goodsClient.querySkuBySpuId(spu.getId());
        if (CollectionUtils.isEmpty(skusList)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FONO);
        }
        //价格集合
        Set<Long> prices = new HashSet<>();
        // 处理sku，仅封装id、价格、标题、图片，并获得价格集合
        List<Map<String, Object>> skuList = new ArrayList<>();
        for (Sku sku : skusList) {
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            skuList.add(skuMap);
            prices.add(sku.getPrice());
        }

        //拿价格
        Set<Long> priceList = skusList.stream().map(Sku::getPrice).collect(Collectors.toSet());

        // 查询详情
        SpuDetail spuDetail = this.goodsClient.querySpuDetailById(spu.getId());
        // 查询规格参数
        List<Specparam> params = specificationClient.queryparamByList(null, spu.getCid3(), "true");
        if (CollectionUtils.isEmpty(params)) {
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
            if (param.getGeneric()) {
                value = genericSpecs.get(param.getId() + "");
//                //判断是否是数值类型
                if (param.getNumeric()) {
                    //处理段
                    value = chooseSegment(value.toString(), param);
                }
            } else {
                value = specialSpecs.get(param.getId());

            }
            specs.put(key, value);
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
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /***
     * @Description: es   搜锁分页封装
     * @Param: [request]
     * @return: java.lang.Object
     * @Author: zhoukx
     * @Date: 2019/5/9
     */
    public PageResult<Goods> search(SearchRequest request) {
        int page = request.getPage() - 1;  //es  从0开始
        int size = request.getSize();
        //创建查询构造器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //结果过滤   不需要的字段过滤器
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subTitle", "skus"}, null));
        //1 分页
        queryBuilder.withPageable(PageRequest.of(page, size));
        //2 过滤
        QueryBuilder basicQuery = buildBasicQuery(request);
        log.info(basicQuery.toString()+"查询过滤器【过滤】");
        queryBuilder.withQuery(basicQuery);
        log.info(queryBuilder.toString()+"查询过滤器{结果}");
        //聚合分类和品牌信息
        //聚合分类
        String categoryAggName = "category_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        //聚合品牌
        String brandAggName = "brand_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        //查询  执行查询的操作
        AggregatedPage<Goods> result = elasticsearchTemplate.queryForPage(queryBuilder.build(), Goods.class);
        log.info(result.toString()+"查询结果");
        //3 解析结果
        long total = result.getTotalElements();
        int totalPage = result.getTotalPages();
        //想要的实体截国
        List<Goods> goodsList = result.getContent();
        //处理聚合结果
        Aggregations aggs = result.getAggregations();
        List<Category> categories = parseCategoryAgg(aggs.get(categoryAggName));
        List<Brand> brands = parseBrandAgg(aggs.get(brandAggName));

        //完成规格参数聚合
        List<Map<String, Object>> specs = null;
        if (categories != null && categories.size() == 1) {
            //商品分类存在的数量为1 ，可以聚合
            specs = buildSpecificationgAgg(categories.get(0).getId(), basicQuery);

        }
        return new SearchResult(total, (long) totalPage, goodsList, categories, brands, specs);
    }

    //封装过滤条件
    private QueryBuilder buildBasicQuery(SearchRequest request) {
        //创建布尔查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // 基本查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()));

        // 整理过滤条件
        Map<String, String> filter = request.getFilter();
        for (Map.Entry<String, String> entry : filter.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // 商品分类和品牌要特殊处理
            if (  !"cid3".equals(key) && !"brandId".equals(key)) {
                key = "specs." + key + ".keyword";
            }
          QueryBuilder q =  QueryBuilders.termQuery(key,value);
            log.info(q.toString()+"封装的方法");
            queryBuilder.filter(QueryBuilders.termsQuery(key,value));
        }


        return queryBuilder;
    }

    private List<Map<String, Object>> buildSpecificationgAgg(Long cid, QueryBuilder basicQuery) {
        try {
            List<Map<String, Object>> specs = new ArrayList<>();
            //查询需要聚合的参数
            List<Specparam> params = this.specificationClient.queryparamByList(null, cid, "true");
            //聚合
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            //在查询基础上聚合
            queryBuilder.withQuery(basicQuery);
            // 聚合规格参数
            params.forEach(p -> {
                String key = p.getName();
                queryBuilder.addAggregation(AggregationBuilders.terms(key).field("specs." + key + ".keyword"));
            });

            // 获取结果
            AggregatedPage result = this.elasticsearchTemplate.queryForPage(queryBuilder.build(), Goods.class);

            // 解析聚合结果
            Aggregations aggs = result.getAggregations();
            for (Specparam param : params) {
                String name = param.getName();
                if(StringUtils.equals("机身颜色",name)){
                     continue;
                }
                StringTerms terms = aggs.get(name);
                List<String> options = terms.getBuckets()
                        .stream().map(b -> b.getKeyAsString()).collect(Collectors.toList());
                //准备map
                Map<String, Object> map = new HashMap<>();
                map.put("k", name);
                map.put("options", options);
                specs.add(map);
            }

            return specs;
        } catch ( Exception e)
        {
            log.error("规格聚合出现异常：", e);
            return null;
        }

    }

    private List<Brand> parseBrandAgg(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets().
                    stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Brand> brands = brandClient.quertBrandByIds(ids);
            return brands;
        } catch (Exception e) {
            log.error("【搜索服务异常】brand：", e);
            return null;
        }

    }

    private List<Category> parseCategoryAgg(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets().
                    stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Category> categories = categoryClient.queryNameByIds(ids);
            return categories;
        } catch (Exception e) {
            log.error("【搜索服务异常】categories：", e);
            return null;
        }
    }


    /*** 
    * @Description:     索引库保存 
    * @Param: [spuId] 
    * @return: void 
    * @Author: zhoukx
    * @Date: 2019/5/22 
    */ 
    public void createOrUpdate(Long spuId) {
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        //构建goods 对象
        try {
            Goods goods = buildGoods(spu);
            //存到索引库
            goodsRepository.save(goods);
           
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*** 
    * @Description:  删除索引
    * @Param: [spuId] 
    * @return: void 
    * @Author: zhoukx
    * @Date: 2019/5/22 
    */ 
    public void deleteIndex(Long spuId) {
        goodsRepository.deleteById(spuId);
    }
}