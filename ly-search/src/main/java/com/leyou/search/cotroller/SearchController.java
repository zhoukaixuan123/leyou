package com.leyou.search.cotroller;

import com.leyou.common.vo.PageResult;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/5/9$
 * @description 索引页面$
 */
@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;
    /***
    * @Description:   es  搜索分页
    * @Param: []
    * @return: org.springframework.http.ResponseEntity<com.leyou.common.vo.PageResult<com.leyou.search.pojo.Goods>>
    * @Author: zhoukx
    * @Date: 2019/5/9
    */
    @PostMapping("page")
    public ResponseEntity<PageResult<Goods>> search(@RequestBody SearchRequest request){
     return    ResponseEntity.ok(searchService.search(request));
    }
}
