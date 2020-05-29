package com.changgou.service;

import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.service
 * @version 1.0
 * @date 2020/5/20
 */
public interface WeixinPayService {

    /**
     *
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    Map<String,String> createNative(Map<String,String> paramter);

    Map<String,String> queryStatus(String out_trade_no);

}
