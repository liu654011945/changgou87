package com.changgou.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.gateway
 * @version 1.0
 * @date 2020/5/14
 */
@Component
public class MyGlobalFilter implements GlobalFilter, Ordered {

    private static final String AUTHORIZE_TOKEN = "Authorization";

    //这里处理权限校验的逻辑
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取request对象
        ServerHttpRequest request = exchange.getRequest();
        //2.获取response对象
        ServerHttpResponse response = exchange.getResponse();

        //3.获取当前的请求路径 判断是否属于要去登录路径，如果是 放行
        String path = request.getURI().getPath();//   /user/login

        if(path.startsWith("/api/user/login")){//去登录
            return chain.filter(exchange);
        }

        //4.判断是否有token  如果没有 直接返回
                //4.1 先从请求参数中获取token  如果没有
                //4.2 再去从头中获取token  如果没有
                //4.3 再去cookie中获取token
        String token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
        if(StringUtils.isEmpty(token)){
            token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        }

        if(StringUtils.isEmpty(token)){
            HttpCookie cookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            if(cookie!=null){
                token = cookie.getValue();
            }
        }

        if(StringUtils.isEmpty(token)){//  没有令牌
            //response.setStatusCode(HttpStatus.UNAUTHORIZED);

            //如果没有令牌就需要重定向到登录的页面
            response.setStatusCode(HttpStatus.SEE_OTHER);//从定向状态码
            response.getHeaders().set("Location","http://localhost:9001/oauth/login?FROM="+request.getURI().toString());//重定向的路径

            return response.setComplete();
        }

        //5.如果有token  不用校验 需要将token 继续传递给下一个微服务，默认的情况下cookie中的数据不会传递给下游
        //将令牌获取到 放入头部中传给给下一个微服务
        request.mutate().header(AUTHORIZE_TOKEN,"bearer "+token);

        /*try {
            JwtUtil.parseJWT(token);//ja      va
        } catch (Exception e) {
            e.printStackTrace();
            //说明校验失败，错误信息
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }*/





        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
