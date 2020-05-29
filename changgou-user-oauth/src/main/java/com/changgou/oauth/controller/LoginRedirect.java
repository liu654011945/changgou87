package com.changgou.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.oauth.controller
 * @version 1.0
 * @date 2020/5/18
 */
@Controller
@RequestMapping(value = "/oauth")
public class LoginRedirect {

    @RequestMapping("/login")
    public String showLoginPage(String FROM, Model model){
        model.addAttribute("from",FROM);
        return "login";
    }
}
