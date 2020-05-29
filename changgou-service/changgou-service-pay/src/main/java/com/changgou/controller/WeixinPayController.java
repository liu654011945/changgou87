package com.changgou.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.service.WeixinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.controller
 * @version 1.0
 * @date 2020/5/20
 */
@RestController
@RequestMapping("/weixin/pay")
public class WeixinPayController {

    @Autowired
    private WeixinPayService weixinPayService;


    /**
     * 生成二维码的链接地址给页面 展示二维码给用户看  /create/native?out_trade_no=1&total-feell=2& from=1
     *
     * @param out_trade_no 订单号
     * @param total_fee    金额
     * @return 有金额  有订单号 有code_url 支付的链接地址
     */
    @RequestMapping("/create/native")
    public Result createNative(@RequestParam Map<String, String> parameter) {
        Map<String, String> resultMap = weixinPayService.createNative(parameter);
        return new Result(true, StatusCode.OK, "生成二维码url成功", resultMap);
    }

    /**
     * 支付状态的查询
     *
     * @param out_trade_no
     * @return
     */
    @GetMapping(value = "/status/query")
    public Result queryStatus(String out_trade_no) {
        Map<String, String> resultMap = weixinPayService.queryStatus(out_trade_no);
        return new Result(true, StatusCode.OK, "查询状态成功", resultMap);
    }


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${mq.pay.exchange.order}")
    private String exchange;
    @Value("${mq.pay.queue.order}")
    private String queue;
    @Value("${mq.pay.routing.key}")
    private String routing;



    @Autowired
    private Environment environment;


    /**
     * /notify/url 是微信调用的 是从统一下单API中畅购填写的，返回值是给微信的一个文档规范的XML格式的字符串
     */
    @RequestMapping("/notify/url")
    public String notifyUrl(HttpServletRequest request) {
        ByteArrayOutputStream outputStream = null;
        try {
            //接收到微信的通知
            //获取数据流中的信息
            ServletInputStream inputStream = request.getInputStream();
            //写流   将数据写入 字符串
            outputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
            String content = new String(outputStream.toByteArray(), "utf-8");
            Map<String, String> map = WXPayUtil.xmlToMap(content);
            System.out.println(map);

            String attach = map.get("attach");//jONS格式的字符串{from:1,username:从页面传递过来}
            Map<String, String> attachMap = JSON.parseObject(attach, Map.class);

            //删除了表
            //cuow le
            //如果是秒杀的支付  发送消息到queu2 如果是普通的支付 发送消息到queue1
            String from = attachMap.get("from");
            switch (from) {
                case "1":
                    //发送消息到 普通订单的队列中去
                    rabbitTemplate.convertAndSend(exchange, routing, JSON.toJSONString(map));
                    break;
                case "2":
                    System.out.println("哈哈哈哈：=================秒杀用户支付===成功");
                    //发送消息到 秒杀订单的队列中
                    rabbitTemplate.convertAndSend(
                            environment.getProperty("mq.pay.exchange.seckillorder"),
                            environment.getProperty("mq.pay.routing.seckillkey"),
                            JSON.toJSONString(map));
                    break;
                default:
                    System.out.println("方式错误");
                    break;
            }

            //删除


            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("return_code", "SUCCESS");
            resultMap.put("return_msg", "OK");
            return WXPayUtil.mapToXml(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "<xml><return_code><![CDATA[FAIL]]></return_code>\n" +
                "  <return_msg><![CDATA[BU OK]]></return_msg>\n" +
                "</xml>";
    }
}
