package com.changgou.oauth.controller;

import com.changgou.oauth.service.LoginService;
import com.changgou.oauth.util.CookieUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.oauth.controller
 * @version 1.0
 * @date 2020/5/16
 */
@RequestMapping("/user")
@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    private static final String grant_type = "password";
    private static final String client_id = "changgou";
    private static final String client_secret = "changgou";


    @RequestMapping("/login")
    public Result login(String username, String password) {
        //1.接收页面传递过来的用户名和密码（用户的用户名和密码）
        //2.模拟postman 再次发送请求申请令牌信息
            //需要传递用户名
            //密码
            //授权类型
            //客户端id
            //客户端秘钥
        Map<String, String> info = loginService.login(username, password, grant_type, client_id, client_secret);
        //3.令牌信息返回再次封装,给页面存储起来

        //再封装 存储到cookie中
        String access_token = info.get("access_token");
        saveCookie(access_token);
        return new Result(true, StatusCode.OK, "生成令牌成功", access_token);


    }
    //Cookie存储的域名
    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    //Cookie生命周期
    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;
    private void saveCookie(String token){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response,cookieDomain,"/","Authorization",token,cookieMaxAge,false);
    }
}
