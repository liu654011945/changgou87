package com.changgou.user.config;

import com.alibaba.fastjson.JSON;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.order.config
 * @version 1.0
 * @date 2020/5/17
 */
@Component
public class TokenDecode {
    private static final String PUBLIC_KEY = "public.key";
    private String getPubKey() {
        Resource resource = new ClassPathResource(PUBLIC_KEY);
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream());
            BufferedReader br = new BufferedReader(inputStreamReader);
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException ioe) {
            return null;
        }
    }

    //获取令牌的信息解析之后就是一个Map
    public Map<String, String> getUserInfo() {

        OAuth2AuthenticationDetails authentication = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();

        String tokenValue = authentication.getTokenValue();//令牌

        Jwt jwt = JwtHelper.decodeAndVerify(tokenValue, new RsaVerifier(getPubKey()));

        //获取Jwt原始内容
        String claims = jwt.getClaims();

        Map<String,String> map = JSON.parseObject(claims, Map.class);

        return map;
    }


    //就是获取用户名
    public String getUsername() {
        return getUserInfo().get("user_name");
    }
}
