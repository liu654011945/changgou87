package com.changgou.user.feign;

import com.changgou.user.pojo.User;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.user.feign
 * @version 1.0
 * @date 2020/5/17
 */


@FeignClient(name="user")
@RequestMapping("/user")
public interface UserFeign {

    //根据用户名获取用户的信息
    @GetMapping("/load/{id}")
    Result<User> findById(@PathVariable(name="id") String id);

    //添加积分
    @GetMapping("/points/add")
    public Result addPoints(@RequestParam(name="username") String username,
                            @RequestParam(name="points") Integer points);
}
