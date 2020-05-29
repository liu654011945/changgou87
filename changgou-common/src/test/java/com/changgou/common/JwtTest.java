package com.changgou.common;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.common
 * @version 1.0
 * @date 2020/5/14
 */
public class JwtTest {
    @Test
    public void createJwt(){
        JwtBuilder builder = Jwts.builder();
        //1.创建头信息
        //默认的不需要设置

        //2.创建载荷
       builder.setId("唯一标识")
                .setSubject("主题")
                .setIssuedAt(new Date())//设置签发的时间
                //.setExpiration(new Date())//设置有效期
                //3.创建签名
                .signWith(SignatureAlgorithm.HS256, "itcast");

        //添加自定义的载荷信息
        Map<String, Object> map = new HashMap<>();
        map.put("mykey","值");
        map.put("address","shenzhen");
        builder.addClaims(map);

        String compact = builder.compact();

        //3.生成了令牌
        System.out.println(compact);

    }

    //解析
    @Test
    public void parseToken(){
        String token="eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiLllK_kuIDmoIfor4YiLCJzdWIiOiLkuLvpopgiLCJpYXQiOjE1ODk0NDExNzYsImFkZHJlc3MiOiJzaGVuemhlbiIsIm15a2V5Ijoi5YC8In0.dc19bHi8Uoaw3DOxITHDuXBYjISexJaOF0AH8wg2naw";
        JwtParser parser = Jwts.parser();
        Jws<Claims> itcast = parser.setSigningKey("itcast").parseClaimsJws(token);
        Claims body = itcast.getBody();//载荷信息
        System.out.println(body);
    }
}
