package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/4/14$
 * @description 业务启动类$
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.leyou.item.mapper") // 扫描mapper包
public class ItemServiceAppliction {
    public static void main(String[] args) {
        SpringApplication.run(ItemServiceAppliction.class);
    }
}
