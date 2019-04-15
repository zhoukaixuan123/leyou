package com.leyou.common.advice;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.ExceptionResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/4/15$
 * @description 统一异常处理$
 */

//默认拦截所有的controller
@ControllerAdvice
public class CommonExceptionHandler {

    /*** 
    * @Description:   拦截异常处理
    * @Param: [] 
    * @return: org.springframework.http.ResponseEntity<java.lang.String> 
    * @Author: zhoukx
    * @Date: 2019/4/15 
    */ 
    @ExceptionHandler(LyException.class)
     public ResponseEntity<ExceptionResult> handletException(LyException e){
        return   ResponseEntity.status(e.getExceptionEnum().getCode()).body(new ExceptionResult(e.getExceptionEnum()));

     }

}
