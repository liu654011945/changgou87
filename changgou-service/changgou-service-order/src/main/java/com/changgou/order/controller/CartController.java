package com.changgou.order.controller;

import com.changgou.order.config.TokenDecode;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.order.controller
 * @version 1.0
 * @date 2020/5/17
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;


    @Autowired
    private TokenDecode tokenDecode;

    /**
     *
     * @param num 购买的数量
     * @param id  要购买的SKU的ID
     * @return
     */
    @RequestMapping(value = "/add")
    public Result add(Integer num, Long id){
        //1.获取当前登录的用户名//todo
        String username= tokenDecode.getUsername();
        //2.调用service 添加购物车
        cartService.add(num,id,username);
        //3.返回
        return new Result(true, StatusCode.OK,"添加购物车成功");
    }
    @RequestMapping(value = "/list")
    public Result<List<OrderItem>> list(){
        //1.获取[当前登录的用户]名//todo
        //获取当前的令牌信息，解析令牌 获取里面的用户名即可
        String username= tokenDecode.getUsername();
        //2.调用service 展示购物车列表
        List<OrderItem> orderItemList = cartService.list(username);
        //3.返回
        return new Result<List<OrderItem>>(true,StatusCode.OK,"查询列表成功",orderItemList);
    }
}
