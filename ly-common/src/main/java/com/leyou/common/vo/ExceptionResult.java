package com.leyou.common.vo;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Data;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/4/15$
 * @description 异常处理$
 */
@Data
public class ExceptionResult {

    private int  stauts;
    private String message;
    private Long timestamp;

    public ExceptionResult(ExceptionEnum e) {
        this.stauts = e.getCode();
        this.message = e.getMsg();
        this.timestamp = System.currentTimeMillis();
    }
}
