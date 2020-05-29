package com.changgou;

import entity.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

/***
 * 商品微服务的启动类
 * @author ljh
 * @packagename com.changgou
 * @version 1.0
 * @date 2020/5/5
 */
@SpringBootApplication
@EnableEurekaClient//启用eureka client
@MapperScan(basePackages = "com.changgou.goods.dao")//使用通用mapper提供的组件扫描
public class GoodsApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodsApplication.class,args);
    }

    @Bean
    public IdWorker idWorker(){
        return new IdWorker(0,0);
    }
}
