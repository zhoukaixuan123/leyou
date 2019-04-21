package com.leyou.upload.confg;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/4/21$
 * @description 配置文件读取$
 */
@Data
@ConfigurationProperties(prefix = "ly.upload")
public class UploadProperties {
    private  String baseUrl;
    private List<String> allowTypes;
}
