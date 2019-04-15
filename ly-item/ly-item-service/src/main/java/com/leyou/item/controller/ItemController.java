package com.leyou.item.controller;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.pojo.Item;
import com.leyou.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/4/14$
 * @description $
 */
@RestController
@RequestMapping("item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/{id}")
    public String getll(@PathVariable("id") String id ){
        return  id;
    }

    @PostMapping()
    public ResponseEntity<Item> getStr(Item item){
        if(item.getStudy_id() == null){
           throw  new LyException(ExceptionEnum.PRICE_CANNOT_BY_NUll);
        }
        item  = itemService.saveItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(item) ;
    }
}
