package com.changgou.search.controller;

import com.changgou.search.feign.SkuFeign;
import com.changgou.search.pojo.SkuInfo;
import entity.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.search.controller
 * @version 1.0
 * @date 2020/5/13
 */
@Controller
@RequestMapping("/search")
public class SkuController {

    //搜索功能实现 接收map对象的参数值 进行调用feign 查询搜索微服务的数据  数据进行组装 返回给页面渲染
    //用getmapting 不要用postmaping
    //map 就是接收页面传递的get请求中的参数列表
    @Autowired
    private SkuFeign skuFeign;

    @GetMapping("/list")
    public String showSearchPage(@RequestParam(required = false) Map searchMap, Model model) {

       /* String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String s = request.getRequestURL().toString();

        String queryString = request.getQueryString();*/

        //1.调用feign 获取搜索的结果集对象
        Map resultMap = skuFeign.search(searchMap);
        //2.设置结果集对象到model中
        model.addAttribute("result", resultMap);
        //3.search.html模板也中通过表达式获取值进行展示


        //4获取到页面传递过来的数据 再添加到model中，html中通过表达式获取model中的值回显
        model.addAttribute("searchMap", searchMap);

        //5 获取页面传递过来的参数名和参数值 并且组装url 返回到model中
        String url = url(searchMap);
        model.addAttribute("url", url);

        //6.查询之后创建一个page 对象设置相关的参数 返回给model中 获取page对象中的lpage rpage 开始循环遍历即可
        //已经计算出来了
        Page<SkuInfo> page = new Page<SkuInfo>(
                Long.valueOf(resultMap.get("total").toString()),
                Integer.valueOf(resultMap.get("pageNum").toString()),
                Integer.valueOf(resultMap.get("pageSize").toString())
        );
        model.addAttribute("page",page);

        return "search";
    }

    private String url(Map<String, String> searchMap) {
        //map中有参数名和值        keywords:华为，category:"电视","spec_网络制式":"移动2G"
        //是通过参数动态拼接的
        String url = "/search/list";
        if (searchMap != null) {
            url += "?";
            for (Map.Entry<String, String> stringStringEntry : searchMap.entrySet()) {
                String key = stringStringEntry.getKey(); // keywords
                String value = stringStringEntry.getValue(); // 华为
                //判断如果是分页的pageNum属性值 就跳过去
                if(key.equals("pageNum")){
                    continue;
                }
                url += key + "=" + value + "&";

            }
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

}
