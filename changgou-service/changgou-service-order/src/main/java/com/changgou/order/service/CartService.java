package com.changgou.order.service;

import com.changgou.order.pojo.OrderItem;

import java.util.List;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.order.service
 * @version 1.0
 * @date 2020/5/17
 */
public interface CartService {
    /**
     * 为某一个用户添加商品到购物车中
     * @param num  购买的数量
     * @param id  购买的商品的SKU的ID
     * @param username 登录的用户
     */
    void add(Integer num, Long id, String username);

    List<OrderItem> list(String username);

}
