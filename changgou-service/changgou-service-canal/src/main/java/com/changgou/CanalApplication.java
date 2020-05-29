package com.changgou;

import com.xpand.starter.canal.annotation.EnableCanalClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou
 * @version 1.0
 * @date 2020/5/9
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//排除数据源的配置
@EnableEurekaClient//eureka的客户端
@EnableCanalClient//启用canal的客户端生效
public class CanalApplication {
    public static void main(String[] args) {
        SpringApplication.run(CanalApplication.class, args);
    }
}
