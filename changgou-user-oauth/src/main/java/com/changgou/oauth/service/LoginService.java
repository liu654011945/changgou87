package com.changgou.oauth.service;

import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.oauth.service
 * @version 1.0
 * @date 2020/5/16
 */
public interface LoginService {
    /**
     * 登录成功申请令牌
     *
     * @param username      用户名
     * @param password      密码
     * @param grant_type    授权类型
     * @param client_id     客户端id
     * @param client_secret 客户端秘钥
     * @return
     */
    Map<String, String> login(String username, String password, String grant_type, String client_id, String client_secret);
}
