package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/4/17$
 * @description 品牌管理$
 */
@Slf4j
@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandListPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
         //分页查询
        PageHelper.startPage(page,rows);
        //过滤条件
        Example example =new Example(Brand.class);
        if(StringUtils.isNotBlank(key)){
            //过滤
            example.createCriteria().orLike("name","%"+key+"%")
                    .orEqualTo("letter",key.toUpperCase());
        }
        //排序
        if(StringUtils.isNotBlank(sortBy)){
            String orderByClause =sortBy +(desc ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
        //查询
        List<Brand> list = brandMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(list)){
            throw  new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //转成这个对象
        PageInfo<Brand> info  = new PageInfo<>(list);

        return  new PageResult<>(info.getTotal(),list);
    }

    /*** 
    * @Description:   保存品牌
    * @Param: [brand, cids] 
    * @return: void 
    * @Author: zhoukx
    * @Date: 2019/4/18
     *
     *  开启事务注解
    */ 
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        // 新增品牌信息
        int count = this.brandMapper.insert(brand);
        if(count != 1){
            throw new LyException( ExceptionEnum.BRAND_SAVE_ERROR );
        }
        // 新增品牌和分类中间表
        for (Long cid : cids) {
            count  =this.brandMapper.insertCategoryBrand(cid, brand.getId());
            if(count != 1){
                throw new LyException( ExceptionEnum.BRAND_SAVE_ERROR );
            }
        }
    }
    public Brand quertById(Long id){
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if(brand == null){
            throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR );
        }
        return  brand;
    }

    public List<Brand> queryBrandByCategory(Long cid) {
        return this.brandMapper.queryByCategoryId(cid);
    }


    public List<Brand> quertByIds(List<Long> ids) {
       List<Brand> brands =  brandMapper.selectByIdList(ids);
       if(CollectionUtils.isEmpty(brands)){
           log.error("Brand品牌信息没有查到++++++++++++");
            throw  new LyException(ExceptionEnum.BRAND_NOT_FOUND);
       }
        return  brands;
    }
}
