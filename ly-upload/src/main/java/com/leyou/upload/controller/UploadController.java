package com.leyou.upload.controller;

import com.leyou.upload.Service.UploadService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/4/20$
 * @description $
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private UploadService uploadService;


    /*** 
    * @Description: 上传图片
    * @Param: [file] 
    * @return: org.springframework.http.ResponseEntity 
    * @Author: zhoukx
    * @Date: 2019/4/20 
    */ 
    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file){
        String url = this.uploadService.upload(file);
        if (StringUtils.isBlank(url)) {
            // url为空，证明上传失败
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // 返回200，并且携带url路径
        return ResponseEntity.ok(url);
    }

}
        /* # 上传路径的映射  饶过网关
        location /api/upload {
               rewrite "^(.*)$" /zuul/$1 ;
        }*/


