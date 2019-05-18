package com.leyou.page.controller;

import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/5/16$
 * @description 配置Thymeleaf模板，跳转详情页面$
 */
@Controller
public class PageController {

    @Autowired
    private PageService pageService;

    @GetMapping("item/{id}.html")
    public  String  toItemPage(@PathVariable("id") Long spuId,Model model){
        //准备模型数据
        Map<String,Object> atteibutes = pageService.loadMpdel(spuId);
        model.addAllAttributes(atteibutes);
        //返回试图
            return "item";
        }


}
