package com.changgou.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/***
 * 描述  拦截器是全局的，在调用feign之前都会执行
 * @author ljh
 * @packagename com.changgou.order.config
 * @version 1.0
 * @date 2020/5/17
 */
@Component
public class MyFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        //1.获取当前的请求对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            //2.循环遍历获取请求对象中的头信息（authonrition的头的信息） 循环遍历所有的头信息 全部传递过去
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();//头的名字
                String headerValue = request.getHeader(headerName);
                //3.再调用feign之前将头添加到Http当中传递给下游微服务
                template.header(headerName, headerValue);
            }
        }
    }
}
