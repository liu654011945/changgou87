package com.changgou.seckill.consumer;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import entity.SystemConstants;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.seckill.consumer
 * @version 1.0
 * @date 2020/5/24
 */
@Component
@RabbitListener(queues = "queue.seckillorder")
public class SeckillOrderPayMessageListener {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedissonClient redissonClient;

    //监听消息 获取数据
    @RabbitHandler
    public void consumeMessage(String message) throws Exception {
        //1.将数据转成JSON
        Map<String, String> resultMap = JSON.parseObject(message, Map.class);
        System.out.println(resultMap);
        if (resultMap != null && resultMap.get("return_code").equals("SUCCESS")) {
            //2.获取里面的数据判断 是否支付成功  //3.如果支付成功   将数据存储到mysql

            String attach = resultMap.get("attach");//json数据（{username:zhangsan}）
            Map<String, String> attachMap = JSON.parseObject(attach, Map.class);


            if (resultMap.get("result_code").equals("SUCCESS")) {
                //todo
                //1.将redis中的订单 存储到数据库中
                SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_ORDER_KEY).get(attachMap.get("username"));
                String time_end = resultMap.get("time_end");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                Date payTime = simpleDateFormat.parse(time_end);
                seckillOrder.setPayTime(payTime);//支付时间
                seckillOrder.setStatus("1");//支付成功
                seckillOrder.setTransactionId(resultMap.get("transaction_id"));
                seckillOrderMapper.insertSelective(seckillOrder);
                //2.将redis中的订单 删除掉（预订单）
                redisTemplate.boundHashOps(SystemConstants.SEC_KILL_ORDER_KEY).delete(attachMap.get("username"));
                //3.删除掉用户的计数的值（让其继续可以下单）
                redisTemplate.boundHashOps(SystemConstants.SEC_KILL_QUEUE_REPEAT_KEY).delete(attachMap.get("username"));
                //4.删除redis的中的用户的抢单的状态信息
                redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).delete(attachMap.get("username"));
            } else {
                //todo
                //4.如果支付失败  简单处理
                //   0. 关闭交易  todo


                //  1. 恢复库存
                SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).get(attachMap.get("username"));
                SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + seckillStatus.getTime()).get(seckillStatus.getGoodsId());
                if(seckillGoods==null){
                    //从数据库查询
                    seckillGoods= seckillGoodsMapper.selectByPrimaryKey(seckillStatus.getGoodsId());
                }

                //上锁
                RLock mylock = redissonClient.getLock("Mylock");
                try {
                    mylock.lock(5, TimeUnit.SECONDS);
                    seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
                    redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + seckillStatus.getTime()).put(seckillStatus.getGoodsId(),seckillGoods);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    mylock.unlock();
                }
                //  2. 删除预订单
                redisTemplate.boundHashOps(SystemConstants.SEC_KILL_ORDER_KEY).delete(attachMap.get("username"));
                // 3 删除掉用户的计数的值（让其继续可以下单）
                redisTemplate.boundHashOps(SystemConstants.SEC_KILL_QUEUE_REPEAT_KEY).delete(attachMap.get("username"));
                // 4 删除redis的中的用户的抢单的状态信息
                redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).delete(attachMap.get("username"));
                System.out.println("需要做1 将预订单进行存储到mysql中，清除redis的预订单");
            }
        }
    }
}
