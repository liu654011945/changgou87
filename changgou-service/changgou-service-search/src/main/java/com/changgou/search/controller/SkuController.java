package com.changgou.search.controller;

import com.changgou.search.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.search
 * @version 1.0
 * @date 2020/5/10
 */
@RestController
@RequestMapping("/search")
public class SkuController {

    @Autowired
    private SkuService skuService;

    /**
     * 导入数据到ES中
     *
     * @return
     */
    @GetMapping("/import")
    public Result importToEs() {
        skuService.importSku();
        return new Result(true, StatusCode.OK, "导入成功");
    }

    /**
     * 根据不定的参数条件 执行搜索 返回map
     *
     * @param searchMap 搜索的条件组合的json数据
     * @return
     */
//    @PostMapping
    @GetMapping
    public Map search(@RequestParam(required = false) Map searchMap) {
        return skuService.search(searchMap);
    }
}
