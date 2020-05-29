package com.changgou.oauth.config;

import com.changgou.user.feign.UserFeign;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/***
 * 描述
 * @author ljh
 * @packagename com.itheima.config
 * @version 1.0
 * @date 2020/1/10
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserFeign userFeign;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("获取到的用户名是：" + username);
        String permission = "ROLE_ADMIN,ROLE_USER";//设置权限
        //todo  需要动态的获取用户的信息 进行匹配
        //1.通过feign调用用户微服务的方法 获取用户的信息
        Result<com.changgou.user.pojo.User> result = userFeign.findById(username);
        com.changgou.user.pojo.User user = result.getData();
        if(user==null){
            System.out.println("用户为空");
            return null;
        }
        //2.判断用户信息是否存在，如果不存在 直接返回null (简单的处理)

        //3.用户信息存在，获取密码 交给spring 框架自动的进行匹配即可
        String password = user.getPassword();//是从数据库查询出来的
        /*return new User(username, passwordEncoder.encode("szitheima"),
                AuthorityUtils.commaSeparatedStringToAuthorityList(permission));*/
        return new User(username, password,
                AuthorityUtils.commaSeparatedStringToAuthorityList(permission));
    }
}
