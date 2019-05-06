package com.leyou.item.api;

import com.leyou.item.pojo.Specparam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SpecificationApi {
    /***
     * @Description:
     * @Param: [gid 组id, cid分类id, searching搜索id]
     * @return: org.springframework.http.ResponseEntity<java.util.List<com.leyou.item.pojo.Specparam>>
     * @Author: zhoukx
     * @Date: 2019/4/28
     */
    @GetMapping("spec/params")
    List<Specparam> queryparamByList(
            @RequestParam(value = "gid" ,required=false) Long gid,
            @RequestParam(value = "cid" ,required=false) Long cid,
            @RequestParam(value = "searching" ,required=false) Boolean searching
    );
}
