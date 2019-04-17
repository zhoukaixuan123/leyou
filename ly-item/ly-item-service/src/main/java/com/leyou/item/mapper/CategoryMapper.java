package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;
/*** 
* @Description:   商品分类接口
* @Param:  
* @return:  
* @Author: zhoukx
* @Date: 2019/4/17 
*/

@Component
public interface CategoryMapper extends Mapper<Category> {
}
