package com.changgou.search.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/***
 * 搜索微服务的feign
 * @author ljh
 * @packagename com.changgou.search.feign
 * @version 1.0
 * @date 2020/5/13
 */
@FeignClient(name="search")
@RequestMapping("/search")
public interface SkuFeign {
    //搜索数据返回结果集
    @GetMapping
    public Map search(@RequestParam(required = false) Map searchMap);
}
