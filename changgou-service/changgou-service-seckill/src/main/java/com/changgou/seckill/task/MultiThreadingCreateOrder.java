package com.changgou.seckill.task;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import entity.IdWorker;
import entity.SystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.seckill.task
 * @version 1.0
 * @date 2020/5/22
 */
@Component
public class MultiThreadingCreateOrder {


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private IdWorker idWorker;


    @Async//异步的 本质就是多线程
    public void createOrder(){
        //模拟下下单操作
        try {
            System.out.println("模拟下单开始================================:"+Thread.currentThread().getName());
            Thread.sleep(10000);


            //从队列中获取用户的信息  todo
            SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps(SystemConstants.SEC_KILL_USER_QUEUE_KEY).rightPop();//
            if(seckillStatus!=null) {
                String time =seckillStatus.getTime();
                Long id = seckillStatus.getGoodsId();
                String username = seckillStatus.getUsername();

                //开始上锁

                //1.根据id获取商品对象数据
                SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + time).get(id);
                //2.判断商品是否存在 判断商品的库存是大于0
               /* if (seckillGoods == null || seckillGoods.getStockCount() <= 0) {
                    throw new RuntimeException("卖完了!");//方便起见直接用RuntimeException
                }
                //3.减库存 重新存储到redis中
                seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);//设置剩余库存为原来的 -1
                redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + time).put(id, seckillGoods);*/


                //释放锁

                //4.判断库存是否大于0 如果是 更新到数据库中 删除redis中的商品
                if (seckillGoods.getStockCount() <= 0) {
                    seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);//更新到数据库
                    redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + time).delete(id);
                }
                //5.下预订单
                SeckillOrder order = new SeckillOrder();
                order.setId(idWorker.nextId());
                order.setSeckillId(id);//购买的商品id
                order.setMoney(seckillGoods.getCostPrice());//单价 一个* 单价
                order.setUserId(username);//设置订单所属的用户
                order.setCreateTime(new Date());
                order.setStatus("0");//未支付

                redisTemplate.boundHashOps(SystemConstants.SEC_KILL_ORDER_KEY).put(username, order);//用户名  一个用户一个订单


                //修改状态
                seckillStatus.setStatus(2);//订单创建成功待支付
                seckillStatus.setOrderId(order.getId());
                seckillStatus.setMoney(Float.valueOf(order.getMoney()));
                redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).put(username,seckillStatus);
            }



        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("模拟下单结束================================:"+Thread.currentThread().getName());
    }
}
