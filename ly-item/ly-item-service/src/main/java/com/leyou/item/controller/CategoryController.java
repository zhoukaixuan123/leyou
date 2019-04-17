package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/4/17$
 * @description 商品分类Controller$
 */
@RestController
@RequestMapping("category")
public class CategoryController
{
    @Autowired
    private CategoryService categoryService;

    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoryListByPid(@RequestParam("pid")Long pid){
        return  ResponseEntity.ok(categoryService.queryCategoryListByPid(pid));
    }


}
