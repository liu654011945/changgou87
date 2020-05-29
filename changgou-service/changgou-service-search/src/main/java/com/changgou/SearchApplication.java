package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou
 * @version 1.0
 * @date 2020/5/10
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//排除掉数据库配置类
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.changgou.goods.feign")//声明启用指定包下的feign
public class SearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
    }
}
