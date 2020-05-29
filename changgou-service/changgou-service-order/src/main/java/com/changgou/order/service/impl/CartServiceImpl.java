package com.changgou.order.service.impl;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.order.service.impl
 * @version 1.0
 * @date 2020/5/17
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SpuFeign spuFeign;

    //dao
    @Override
    public void add(Integer num, Long id, String username) {

        if(num<=0){
            //说明你不想买了 就删除掉
            redisTemplate.boundHashOps("Cart_"+username).delete(id);
            return;
        }


        //1.通过feign来调用根据商品的ID 获取商品SKU的数据
        Result<Sku> skuResult = skuFeign.findById(id);
        Sku sku = skuResult.getData();
        //2.添加OrderItem数据对象
        OrderItem orderItem = new OrderItem();
        orderItem.setSkuId(id);
        orderItem.setSpuId(sku.getSpuId());
        orderItem.setName(sku.getName());
        orderItem.setPrice(sku.getPrice());//价格
        orderItem.setNum(num);//数量
        orderItem.setImage(sku.getImage());//图片
        orderItem.setMoney(sku.getPrice()*num);//金额
        orderItem.setPayMoney(sku.getPrice()*num);//金额
        //categoryid1 2 3  根据spuid通过调用feign 获取spu的对象 再获取1 2 3 分类的ID
        Result<Spu> spuResult = spuFeign.findById(sku.getSpuId());
        Spu spu = spuResult.getData();
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());
        //3.添加orderItem 添加到redis(购物车)中
        redisTemplate.boundHashOps("Cart_"+username).put(id,orderItem);

    }

    @Override
    public List<OrderItem> list(String username) {
        //所有的数据
        return redisTemplate.boundHashOps("Cart_" + username).values();
    }
}
