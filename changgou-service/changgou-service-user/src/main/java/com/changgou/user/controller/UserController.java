package com.changgou.user.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.user.pojo.User;
import com.changgou.user.service.UserService;
import com.github.pagehelper.PageInfo;
import entity.BCrypt;
import entity.JwtUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    /***
     * User分页条件搜索实现
     * @param user
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@RequestBody(required = false) User user, @PathVariable(name = "page") int page, @PathVariable(name = "page") int size) {
        //调用UserService实现分页条件查询User
        PageInfo<User> pageInfo = userService.findPage(user, page, size);
        return new Result(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     * User分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@PathVariable(name = "page") int page, @PathVariable(name = "size") int size) {
        //调用UserService实现分页查询User
        PageInfo<User> pageInfo = userService.findPage(page, size);
        return new Result<PageInfo>(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param user
     * @return
     */
    @PostMapping(value = "/search")
    public Result<List<User>> findList(@RequestBody(required = false) User user) {
        //调用UserService实现条件查询User
        List<User> list = userService.findList(user);
        return new Result<List<User>>(true, StatusCode.OK, "查询成功", list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable(name = "id") String id) {
        //调用UserService实现根据主键删除
        userService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /***
     * 修改User数据
     * @param user
     * @param id
     * @return
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody User user, @PathVariable(name = "id") String id) {
        //设置主键值
        user.setUsername(id);
        //调用UserService实现修改User
        userService.update(user);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /***
     * 新增User数据
     * @param user
     * @return
     */
    @PostMapping
    public Result add(@RequestBody User user) {
        //调用UserService实现添加User
        userService.add(user);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    /***
     * 根据ID查询User数据 只能是某一个角色的人才能查看
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<User> findById(@PathVariable(name = "id") String id) {
        //调用UserService实现根据主键查询User
        User user = userService.findById(id);
        return new Result<User>(true, StatusCode.OK, "查询成功", user);
    }

    /***
     * 查询User全部数据  可能需要有管理员省份的人才能查看
     * @return
     */
    @GetMapping
    @PreAuthorize(value="hasAuthority('ROLE_ADMIN')")//该注解用于在方法执行之前执行判断 如果有权限才能执行该方法
    public Result<List<User>> findAll() {
        //调用UserService实现查询所有User
        List<User> list = userService.findAll();
        return new Result<List<User>>(true, StatusCode.OK, "查询成功", list);
    }

    /**
     * 用户名和密码登录  后台密码是需要加密存储的，加密算法是bcrtyp
     *
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/login")
    public Result login(String username, String password, HttpServletResponse response) {
        //判断
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return new Result(false, StatusCode.LOGINERROR, "登录失败");
        }
        //根据用户名查询用户数据库的数据
        User user = userService.findById(username);
        //判断用户名是否存在
        if (user == null) {
            return new Result(false, StatusCode.LOGINERROR, "登录失败,用户名或密码错误");
        }
        // 判断密码是否正确  数据库的密码是加密的bcrypt

        if (!BCrypt.checkpw(password, user.getPassword())) {//不正确
            return new Result(false, StatusCode.LOGINERROR, "登录失败,用户名或密码错误");
        }

        //登录成功需要颁发令牌
        Map<String, Object> map = new HashMap<>();
        map.put("role", "ROLE_SUPER");
        map.put("success", "true");
        map.put("username", username);
        String token = JwtUtil.createJWT(UUID.randomUUID().toString(), JSON.toJSONString(map), null);

        //存储到用户的兜里 cookie
        Cookie cookie = new Cookie("Authorization",token);
        cookie.setPath("/");//设置路径
        response.addCookie(cookie);

        return new Result(true, StatusCode.OK, "登录成功",token);
    }

    /**
     * 根据用户名获取用户的信息
     * @param id 用户名
     * @return
     */
    @GetMapping("/load/{id}")
    Result<User> loadById(@PathVariable(name="id") String id){
        User user = userService.findById(id);
        return new Result<User>(true,StatusCode.OK,"查询用户的信息成功",user);
    }


    /**
     * 加积分
     * @param username
     * @param points
     * @return
     */
    @GetMapping("/points/add")
    public Result addPoints(@RequestParam(name="username") String username,
                            @RequestParam(name="points") Integer points){
        //受影响的行
        Integer count = userService.addPoints(username,points);
        if(count>0){
            return new Result(true,StatusCode.OK,"添加积分成功");
        }else{
            return new Result(false,StatusCode.ERROR,"添加积分失败");
        }
    }


    public static void main(String[] args) {
        String changgou = new BCryptPasswordEncoder().encode("changgou");
        System.out.println(changgou);

    }
}
