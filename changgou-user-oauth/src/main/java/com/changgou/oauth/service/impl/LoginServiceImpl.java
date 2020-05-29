package com.changgou.oauth.service.impl;

import com.changgou.oauth.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.oauth.service.impl
 * @version 1.0
 * @date 2020/5/16
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public Map<String, String> login(String username, String password, String grant_type, String client_id, String client_secret) {
        //0 定义一个请求 http://localhost:9001/oauth/token

        String url = "http://localhost:9001/oauth/token";


        //1. 创建请求体对象
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("username",username);
        body.add("password",password);
        body.add("grant_type",grant_type);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

        headers.add("Authorization","Basic "+Base64.getEncoder().encodeToString(new String(client_id+":"+client_secret).getBytes()));

        HttpEntity<MultiValueMap<String,String>> requestEntity = new HttpEntity<MultiValueMap<String,String>>(body,headers);

        //3.resttemplate 模拟postman 再次发送请求申请令牌信息
            //需要传递用户名
            //密码
            //授权类型
            //客户端id
            //客户端秘钥
        ResponseEntity<Map> entity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

        Map body1 = entity.getBody();

        return body1;
    }

    public static void main(String[] args) {
        byte[] decode = Base64.getDecoder().decode(new String("Y2hhbmdnb3UxMjMyMTpjaGFuZ2dvdTEyMzEzMjE=").getBytes());
        String s = new String(decode);
        System.out.println(s);

        String s1 = Base64.getEncoder().encodeToString(new String("changgou12321:changgou1231321").getBytes());
        System.out.println(s1);
    }
}
