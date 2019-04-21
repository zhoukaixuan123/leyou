package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

/*** 
* @Description:  品牌管理
* @Param:  
* @return:  
* @Author: zhoukx
* @Date: 2019/4/17 
*/
@Component
public interface BrandMapper extends Mapper<Brand> {

    //保存 中间表信息
    @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES (#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);
}
