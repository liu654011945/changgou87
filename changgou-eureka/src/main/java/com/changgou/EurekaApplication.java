package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou
 * @version 1.0
 * @date 2020/5/5
 */
@SpringBootApplication
@EnableEurekaServer//启用eurek的服务端的生效
public class EurekaApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class, args);
    }
}
