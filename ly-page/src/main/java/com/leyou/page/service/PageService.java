package com.leyou.page.service;

import com.leyou.item.pojo.*;
import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
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
@Slf4j
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
    //页面静态化实现类
    @Autowired
    private TemplateEngine engine;



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
        map.put("spu", spu);
        map.put("skus", skus);
        map.put("detail", detail);
        map.put("brand", brand);
        map.put("categories", categories);
        map.put("specs", specs);
        return map;
    }

    //页面静态化
    public void createHtml(Long spuId) {
        //初始化输出流
        PrintWriter writer = null;
        try {
            //初始化上下  文
            Context context = new Context();
            //初始化数据
            Map<String, Object> map = this.loadMpdel(spuId);
            //把数据放到context 中
            context.setVariables(map);
            //写入文件
            File file = new File("F:\\upload\\page",spuId+".html");
            if(file.exists()){
                file.delete();
            }
            writer = new PrintWriter(file,"UTF-8");
            engine.process("item", context, writer);
        } catch (Exception ex) {

            log.error(   "创建模板出错：" +spuId);
        } finally {
            if(writer != null ){
                writer.close();
            }

        }

    }

    public void deleteHtml(Long spuId) {
        File file = new File("F:\\upload\\page",spuId+".html");
       if(file.exists()){
           file.delete();
       }
    }
}
