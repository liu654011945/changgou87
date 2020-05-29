package com.changgou.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.service.WeixinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.service.impl
 * @version 1.0
 * @date 2020/5/20
 */
@Service
public class WeixinPayServiceImpl implements WeixinPayService {


    @Value("${weixin.appid}")
    private String appid;

    @Value("${weixin.partner}")
    private String partner;

    @Value("${weixin.partnerkey}")
    private String partnerkey;

    @Value("${weixin.notifyurl}")
    private String notifyurl;


    @Override
    public Map<String, String> createNative(Map<String,String> parameter) {
        try {
            //1.引入httpcliernt的依赖 封装一个httpclient的工具类 的方法
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            //2.设置请求参数 将map转成xml
            Map<String, String> paraMap = new HashMap();
            paraMap.put("appid", appid);
            paraMap.put("mch_id", partner);
            paraMap.put("nonce_str", WXPayUtil.generateNonceStr());//安全性不高
            //签名不管 在进行转换的时候直接添加签名
            paraMap.put("body", "畅购");
            paraMap.put("out_trade_no", parameter.get("out_trade_no"));
            paraMap.put("total_fee", parameter.get("total_fee"));//金额 单位是分
            paraMap.put("spbill_create_ip", "127.0.0.1");
            paraMap.put("notify_url", notifyurl);//异步通知的回调地址 /notify/url
            paraMap.put("trade_type", "NATIVE");

            paraMap.put("attach", JSON.toJSONString(parameter));//携带attach数据 吧{out_trade_no,total_fel,from:1}
            String xml = WXPayUtil.generateSignedXml(paraMap, partnerkey);

            httpClient.setXmlParam(xml);
            //3 模拟浏览器发送https请求 模拟浏览器接收响应获取code_url
            httpClient.post();
            String content = httpClient.getContent();//xml-->转成map
            System.out.println(content);//微信返回回来的数据
            Map<String, String> map = WXPayUtil.xmlToMap(content);

            //4 封装一个map再返回
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("out_trade_no", parameter.get("out_trade_no"));
            resultMap.put("total_fee", parameter.get("total_fee"));
            resultMap.put("code_url", map.get("code_url"));
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    //查询支付订单的状态
    @Override
    public Map<String, String> queryStatus(String out_trade_no) {

        try {
            //1.组织参数map
            Map<String, String> paraMap = new HashMap();
            paraMap.put("appid", appid);
            paraMap.put("mch_id", partner);
            paraMap.put("nonce_str", WXPayUtil.generateNonceStr());//安全性不高
            //签名不管 在进行转换的时候直接【添加签名】
            paraMap.put("out_trade_no", out_trade_no);//要根据交易订单号查询
            //2.map转出xml 会自动添加签名
            String xml = WXPayUtil.generateSignedXml(paraMap, partnerkey);

            //3.创建httpclient对象 设置值 模拟浏览器发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xml);
            httpClient.post();
            //4.模拟浏览器接收响应
            String content = httpClient.getContent();
            System.out.println(content);
            //5.XML 转出map 返回
            Map<String, String> map = WXPayUtil.xmlToMap(content);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }
}
