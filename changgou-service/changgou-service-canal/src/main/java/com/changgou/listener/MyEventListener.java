package com.changgou.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.*;

import java.util.List;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.listener
 * @version 1.0
 * @date 2020/5/9
 */
@CanalEventListener//事件监听注解  当CRUD发生的时候 起作用
public class MyEventListener {

   /* @InsertListenPoint//当发生了insert语句的时候 触发一下的代码执行
    public void onEvent(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //do something...
    }*/

    @UpdateListenPoint//当发生了update语句的时候 触发一下的代码执行
    public void onEvent1(CanalEntry.RowData rowData) {
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();//获取修改前的数据

        for (CanalEntry.Column column : beforeColumnsList) {
            System.out.println(column.getName()+":"+column.getValue());//打印获取到的列名和列所对应的值
        }

        System.out.println("==================================");
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();//获取修改后的数据

        for (CanalEntry.Column column : afterColumnsList) {
            System.out.println(column.getName()+":"+column.getValue());//打印获取到的列名和列所对应的值
        }

        //真正的业务处理了 ：同步数据到redis中
    }

   /* @DeleteListenPoint //当发生了delete语句的时候 触发一下的代码执行
    public void onEvent3(CanalEntry.EventType eventType) {
        //do something...
    }*/

    //客制化的事件监听
    /*@ListenPoint(destination = "example", schema = "canal-test", table = {"t_user", "test_table"}, eventType = CanalEntry.EventType.UPDATE)
    public void onEvent4(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //do something...
    }*/
}
