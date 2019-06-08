package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.dto.CartDto;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;

    public PageResult<Spu> querySpuByPageAndSort(Integer page, Integer rows, Boolean saleable, String key) {
        //分页
        PageHelper.startPage(page, rows);
        //过滤
        Example example = new Example(Spu.class);
        //搜索字段过滤
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        //上下架过滤
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }

        // 默认排序
        example.setOrderByClause("last_update_time Desc");

        //查询
        List<Spu> spus = spuMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(spus)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //解析分类和品牌的名称
        loadCategoryAndBrandName(spus);

        //解析分页结果
        PageInfo<Spu> info = new PageInfo<>(spus);
        return new PageResult<>(info.getTotal(), spus);
    }

    private void loadCategoryAndBrandName(List<Spu> spus) {
        for (Spu spu : spus) {
            //处理分类名称  用流的方式处理  LIST id 查询
            List<String> name = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream().map(Category::getName).collect(Collectors.toList());
//            字符串拼接  工具 类   join
            spu.setCname(StringUtils.join(name, "/"));
            //处理品牌名称
            spu.setBname(brandService.quertById(spu.getBrandId()).getName());

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
        spu.setId(null);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setSaleable(true);
        spu.setValid(false);

        int count = this.spuMapper.insert(spu);
        if (count != 1) {
            throw new LyException(ExceptionEnum.CREAT_CATE_SB);
        }

        // 新增deteil对象
        SpuDetail detail = spu.getSpuDetail();
        detail.setSpecTemplate(spu.getSpuDetail().getSpecialSpec());
        detail.setSpecifications(spu.getSpuDetail().getDescription());
        detail.setSpuId(spu.getId());

        this.spuDetailMapper.insert(detail);

        // 新增sku
        // saveSkuAndStock(spu.getSkus(), spu.getId());
        //定义库存集合
        List<Stock> stockList = new ArrayList<>();
        //新增sku
        List<Sku> skus = spu.getSkus();
        for (Sku sku : skus) {
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());

            count = skuMapper.insert(sku);
            if (count != 1) {
                throw new LyException(ExceptionEnum.CREAT_CATE_SB);
            }


            //新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());

            stockList.add(stock);

        }

        //批量邢新增库存
        stockMapper.insertList(stockList);
        //发送mq消息
        amqpTemplate.convertAndSend("item.insert",spu.getId());

    }

    public SpuDetail querySpuDetailById(Long id) {
        return this.spuDetailMapper.selectByPrimaryKey(id);
    }

    public List<Sku> querySkuBySpuId(Long spuId) {
        // 查询sku
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(record);
        for (Sku sku : skus) {
            // 同时查询出库存
            sku.setStock(this.stockMapper.selectByPrimaryKey(sku.getId()).getStock());
        }
        return skus;
    }

    public void update(Spu spu) {
        // 查询以前sku
        List<Sku> skus = this.querySkuBySpuId(spu.getId());
        // 如果以前存在，则删除
        if (!CollectionUtils.isEmpty(skus)) {
            List<Long> ids = skus.stream().map(s -> s.getId()).collect(Collectors.toList());
            // 删除以前库存
            Example example = new Example(Stock.class);
            example.createCriteria().andIn("skuId", ids);
            this.stockMapper.deleteByExample(example);

            // 删除以前的sku
            Sku record = new Sku();
            record.setSpuId(spu.getId());
            this.skuMapper.delete(record);

        }
        // 新增sku和库存
        saveSkuAndStock(spu.getSkus(), spu.getId());

        //发送mq消息
        amqpTemplate.convertAndSend("item.update",spu.getId());

        // 更新spu
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);
        spu.setValid(null);
        spu.setSaleable(null);
        this.spuMapper.updateByPrimaryKeySelective(spu);

        // 更新spu详情
        this.spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());

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
            int count = this.skuMapper.insert(sku);
            if (count != 1) {
                throw new LyException(ExceptionEnum.CREAT_CATE_SB);
            }
            // 保存库存信息
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insert(stock);
        }

    }

    public Spu querySpuById(Long id) {
        //查询spu
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu == null) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //查询sku
        spu.setSkus(querySkuBySpuId(id));
        //查询deatil
        spu.setSpuDetail(querySpuDetailById(id));
        return spu;
    }

    public List<Sku> querySkuBySpuIds(List<Long> ids) {
        List<Sku> skus = skuMapper.selectByIdList(ids);
        if(CollectionUtils.isEmpty(skus)){
            throw  new LyException(ExceptionEnum.CATEGORY_NOT_FONO) ;
        }
        //查询库存
        List<Stock> stocksList = stockMapper.selectByIdList(ids);
        if(CollectionUtils.isEmpty(stocksList)){
            throw  new LyException(ExceptionEnum.GOODS_NOT_FOUND) ;
        }
        //我们把stock变成一个map，其key是：sku的id  ，值是库存
        Map<Long,Integer> stockMap = stocksList.stream()
                .collect(Collectors.toMap(Stock::getSkuId,Stock::getStock));
        skus.forEach(s -> s.setStock(stockMap.get(s.getId())));
        return  skus;

    }

    @Transactional(rollbackFor = Exception.class)
    public void decreaseStock(List<CartDto> cartDtos) {
        for (CartDto cart : cartDtos) {
            //查库存
            int count = stockMapper.decreaseStock(cart.getSkuId(), cart.getNum());
            if (count != 1) {
                throw new LyException(ExceptionEnum.STOCK_NOT_FOUNT);
            }
        }
    }
}
