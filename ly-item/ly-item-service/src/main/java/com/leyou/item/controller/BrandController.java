package com.leyou.item.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/4/17$
 * @description 品牌管理$
 */
@RestController
@RequestMapping("brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandListPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
            @RequestParam(value = "key", required = false) String key
    ){

        return  ResponseEntity.ok(brandService.queryBrandListPage(page,rows,sortBy,desc,key));
    }

    @PostMapping()
    public ResponseEntity<Void>  saveBrand(Brand brand,@RequestParam("cids") List<Long> cids){
        this.brandService.saveBrand(brand, cids);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    /**
     * 根据分类查询品牌
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandListByCid(@PathVariable("cid")Long cid){

        List<Brand> brandList = this.brandService.queryBrandByCategory(cid);
        if(CollectionUtils.isEmpty(brandList)){
            // 响应404
            return ResponseEntity.badRequest().build();
        }
        // 响应2 00
        return ResponseEntity.ok(brandList);
    }

    /**
     * 根据id 查询品牌
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id") Long id){
         return  ResponseEntity.ok(brandService.quertById(id));
    }

}
