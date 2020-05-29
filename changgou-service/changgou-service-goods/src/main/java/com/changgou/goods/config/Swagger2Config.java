package com.changgou.goods.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashSet;

/**
 * Created by zhangyuhong
 * Date:2020/5/3
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {
//    查看api方式  http://localhost:18081/swagger-ui.html
    //网页api文档自动生成
    @Bean
    public Docket webApiConfig() {
        HashSet<String> strings = new HashSet<>();
        strings.add("application/json");

        return new Docket(DocumentationType.SWAGGER_2)
//                .groupName("webApi")
                .apiInfo(webApiInfo()).produces(strings)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.changgou.goods.controller"))
                .paths(PathSelectors.any())
                .build();
    }
    //api的信息
    private ApiInfo webApiInfo() {
        return new ApiInfoBuilder()
                .title("商品API文档")
                .description("本文档描述了商品相关的接口定义")
                .version("1.0")
                .contact(new Contact("张康木", "https://blog.csdn.net/weixin_41805792", "1137783348@qq.com"))
                .build();
    }
}
