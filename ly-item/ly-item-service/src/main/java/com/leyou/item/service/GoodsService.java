package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.Stock;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/4/24$
 * @description 商品列表$
 */
@Service
public class GoodsService {

    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private  CategoryService categoryService;
    @Autowired
    private  BrandService  brandService;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;

    public PageResult<Spu> querySpuByPageAndSort(Integer page, Integer rows, Boolean saleable, String key) {
        //分页
        PageHelper.startPage(page,rows);
        //过滤
        Example example = new Example(Spu.class);
        //搜索字段过滤
       Example.Criteria criteria =  example.createCriteria();
        if(StringUtils.isNotBlank(key)){
             criteria.andLike("title","%"+key+"%");
        }
        //上下架过滤
        if(saleable != null){
            criteria.andEqualTo("saleable",saleable);
        }

        // 默认排序
        example.setOrderByClause("last_update_time Desc");

        //查询
       List<Spu> spus =  spuMapper.selectByExample(example);
       if(CollectionUtils.isEmpty(spus)){
           throw  new LyException(ExceptionEnum.GOODS_NOT_FOUND);
       }
       //解析分类和品牌的名称
        loadCategoryAndBrandName(spus);

       //解析分页结果
        PageInfo<Spu> info = new PageInfo<>(spus);
   return  new PageResult<>(info.getTotal(),spus);
    }

    private void loadCategoryAndBrandName(List<Spu> spus) {
        for(Spu spu:spus){
            //处理分类名称  用流的方式处理  LIST id 查询
           List<String> name =  categoryService.queryByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()))
             .stream().map(Category::getName).collect(Collectors.toList());
//            字符串拼接  工具 类   join
            spu.setCname(StringUtils.join(name,"/"));
            //处理品牌名称
           spu.setBname( brandService.quertById(spu.getBrandId()).getName());

        }
    }

    /*** 
    * @Description: 新增商品
    * @Param: [spu] 
    * @return: void 
    * @Author: zhoukx
    * @Date: 2019/4/28 
    */ 
    @Transactional
    public void save(Spu spu) {
        // 保存spu
        spu.setSaleable(true);
        spu.setValid(true);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
       int count =  this.spuMapper.insert(spu);
       if(count != 1){
            throw  new  LyException(ExceptionEnum.CREAT_CATE_SB);
       }
        // 保存spu详情
        spu.getSpuDetail().setSpuId(spu.getId());
        this.spuDetailMapper.insert(spu.getSpuDetail());

        // 保存sku和库存信息
        saveSkuAndStock(spu.getSkus(), spu.getId());
    }

    private void saveSkuAndStock(List<Sku> skus, Long id) {
        for (Sku sku : skus) {
            if (!sku.getEnable()) {
                continue;
            }
            // 保存sku
            sku.setSpuId(id);
            // 初始化时间
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            int count =  this.skuMapper.insert(sku);
            if(count != 1){
                throw  new  LyException(ExceptionEnum.CREAT_CATE_SB);
            }
            // 保存库存信息
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insert(stock);
        }
    }
}
