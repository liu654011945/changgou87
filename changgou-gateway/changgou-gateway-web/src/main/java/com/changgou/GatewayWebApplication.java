package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou
 * @version 1.0
 * @date 2020/5/14
 */
@SpringBootApplication
@EnableEurekaClient
public class GatewayWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayWebApplication.class,args);
    }

    //以ip为参照物进行限流
    @Bean(name = "ipKeyResolver")
    public KeyResolver userKeyResolver(){
        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                //需要获取远程客户端的ip地址
                String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();//获取到远程的地址的Ip

                return Mono.just(ip);//以Ip为基准

            }
        };
    }
}
