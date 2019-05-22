package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 *功能描述
 * @author zhoukx
 * @date 2019/4/14$
 * @description eureka注册中心$
 */
@EnableEurekaServer
@SpringBootApplication
public class LyRegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LyRegistryApplication.class);
    }
}
