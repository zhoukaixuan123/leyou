package com.leyou.item.service;

import com.leyou.item.pojo.Item;

import org.springframework.stereotype.Service;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/4/14$
 * @description 测试$
 */
@Service
public class ItemService {

      public Item saveItem(Item ite){
          ite.setStudy_id("2");
          ite.setUser_name("你好");
          return  ite;
      }
}
