package com.leyou.item.controller;

import com.leyou.item.pojo.Item;
import com.leyou.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null ) ;
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(item) ;
    }
}
