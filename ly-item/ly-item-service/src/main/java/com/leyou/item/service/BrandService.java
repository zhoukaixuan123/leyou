package com.leyou.item.service;

import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/4/17$
 * @description 品牌管理$
 */
@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandListPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
     return  null;
    }
}
