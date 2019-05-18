package com.leyou.item.controller;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.pojo.Specification;
import com.leyou.item.pojo.Specparam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *    根据分类Id查分组
 *
 *
 */
@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    /*** 
    * @Description:   查询商品规则组 
    * @Param: [id] 
    * @return: org.springframework.http.ResponseEntity<java.util.List<com.leyou.item.pojo.Specification>> 
    * @Author: zhoukx
    * @Date: 2019/4/23 
    */ 
    @GetMapping("/groups/{cid}")
    public ResponseEntity<List<Specification>> querySpecificationByCategoryId(@PathVariable("cid") Long id){
        return ResponseEntity.ok(this.specificationService.queryById(id));
    }


    /***
    * @Description:
    * @Param: [gid 组id, cid分类id, searching搜索id]
    * @return: org.springframework.http.ResponseEntity<java.util.List<com.leyou.item.pojo.Specparam>>
    * @Author: zhoukx
    * @Date: 2019/4/28
    */
    @GetMapping("/params")
     public ResponseEntity<List<Specparam>>  queryparamByList(
               @RequestParam(value = "gid" ,required=false) Long gid,
               @RequestParam(value = "cid" ,required=false) Long cid,
               @RequestParam(value = "searching" ,required=false) String searching
    ){
        return  ResponseEntity.ok(specificationService.queryparamByGid(gid,cid,searching));
     }

    // 查询规格参数组，及组内参数
    @GetMapping("group")
    public ResponseEntity<List<Specification>> queryListById(@RequestParam("cid") Long cid){
         return  ResponseEntity.ok(specificationService.queryListByCid(cid));
    }
}