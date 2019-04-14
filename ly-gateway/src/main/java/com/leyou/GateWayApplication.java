package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/4/14$
 * @description 网关 zuul$
 */

@EnableZuulProxy
@SpringCloudApplication
public class GateWayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GateWayApplication.class);
    }

}
