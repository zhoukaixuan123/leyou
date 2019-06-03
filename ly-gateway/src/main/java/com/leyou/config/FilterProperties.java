package com.leyou.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 功能描述
 *
 * @author zhoukx
 * @date 2019/6/3$
 * @description 白名单$
 */

@ConfigurationProperties(prefix = "ly.filter")
public class FilterProperties {


    private List<String> allowPaths;

    public List<String> getAllowPaths() {
        return allowPaths;
    }

    public void setAllowPaths(List<String> allowPaths) {
        this.allowPaths = allowPaths;
    }
}
