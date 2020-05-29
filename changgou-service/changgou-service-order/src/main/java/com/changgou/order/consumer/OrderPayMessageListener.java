package com.changgou.order.consumer;

import com.alibaba.fastjson.JSON;
import com.changgou.order.dao.OrderMapper;
import com.changgou.order.pojo.Order;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/***
 * 监听类用于监听消息 处理业务逻辑（更新状态）
 * @author ljh
 * @packagename com.changgou.order.consumer
 * @version 1.0
 * @date 2020/5/21
 */
@Component
@RabbitListener(queues = "queue.order")
public class OrderPayMessageListener {

    @Autowired
    private OrderMapper orderMapper;

    //接收消息
    @RabbitHandler
    public void jieshoumsg(String msg) {
        System.out.println(msg);
        int i=1/0;
        //获取消息 转出JSON
        Map<String, String> map = JSON.parseObject(msg, Map.class);
        if (map != null && "SUCCESS".equals(map.get("return_code"))) {
            String out_trade_no = map.get("out_trade_no");//订单号
            //判断业务是否支付成功 如果支付成功 更新状态
            if ( "SUCCESS".equals(map.get("result_code"))) {
                //支付成功 需要更新支付时间 支付流水号 支付的状态
                Order order = orderMapper.selectByPrimaryKey(out_trade_no);
                order.setPayStatus("1");//已经支付
                String time_end = map.get("time_end");//支付时间
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                Date date= null;
                try {
                    date = simpleDateFormat.parse(time_end);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                order.setPayTime(date);//设置支付时间 time_end
                order.setTransactionId(map.get("transaction_id"));//支付流水号
                orderMapper.updateByPrimaryKeySelective(order);
            } else {
                Order order = orderMapper.selectByPrimaryKey(out_trade_no);
                order.setIsDelete("1");//已经删除
                //如果是支付失败 删除订单
                orderMapper.updateByPrimaryKeySelective(order);//逻辑删除
            }
        }


    }
}
