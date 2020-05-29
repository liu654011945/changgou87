package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.goods.feign
 * @version 1.0
 * @date 2020/5/10
 */

@FeignClient(name="goods")
@RequestMapping("/sku")
public interface SkuFeign {
    //查询sku表的所有的符合条件的数据
    @GetMapping("/status/{status}")
    Result<List<Sku>> findByStatus(@PathVariable(name="status") String status);

    //根据id获取sku的数据
    @GetMapping(value = "/{id}")
    public Result<Sku> findById(@PathVariable(value = "id", required = true) Long id);

    //减库存
    @GetMapping("/decCount")
    public Result decCount(@RequestParam(name="id") Long id, @RequestParam(name="num") Integer num);


}
