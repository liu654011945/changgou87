package com.changgou.seckill.timer;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import entity.DateUtil;
import entity.SystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.seckill.timer
 * @version 1.0
 * @date 2020/5/22
 */
@Component
public class SeckillGoodsPushTask {


    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    //定时执行的方法
    @Scheduled(cron = "0/5 * * * * ? ")//注解 标识该方法将来要定时执行
    public void loadGoodsPushRedis() {
        //1.循环遍历以当前时间为基准的5个时间段     10:00 当前时间  12:00 14:00 16:00 18:00

        List<Date> dateMenus = DateUtil.getDateMenus();
        for (Date dateMenu : dateMenus) {//第一个循环的时候
            //2.查询符合条件的商品的数据
            String time = DateUtil.data2str(dateMenu, DateUtil.PATTERN_YYYYMMDDHH);//"2020052210"
            Example example = new Example(SeckillGoods.class);//from tb_sekcill_goods
            Example.Criteria criteria = example.createCriteria();//where .......
            criteria.andEqualTo("status", "1");//status=1
            //是POJO的属性
            criteria.andGreaterThan("stockCount", 0);//stock_count>0
            criteria.andEqualTo("startTime", dateMenu);// starttime=10:00
            criteria.andLessThan("endTime", DateUtil.addDateHour(dateMenu, 2));//结束时间=12:00+2小时

            Set keys = redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + time).keys();//获取所有的field

            if(keys!=null && keys.size()>0) {
                criteria.andNotIn("id", keys);
            }

            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);//select *
            //3.将其压入到redis中

            for (SeckillGoods seckillGood : seckillGoods) {
                redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX+time).put(seckillGood.getId(),seckillGood);
                //redisTemplate.expireAt(SystemConstants.SEC_KILL_GOODS_PREFIX+time,DateUtil.addDateHour(dateMenu, 2));//todo
            }

            //给某一个key设置一个过期时间  2个小时

            //第一个参数  设置过期的key
            //第二个参数  设置过期的时间
            //第三个参数  过期时间的单位
            redisTemplate.expire(SystemConstants.SEC_KILL_GOODS_PREFIX+time,2, TimeUnit.HOURS);


        }
    }
}
