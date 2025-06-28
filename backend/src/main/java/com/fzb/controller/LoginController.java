package com.fzb.controller;

import com.fzb.pojo.Result;
import com.fzb.pojo.User;
import com.fzb.service.UserService;
import com.fzb.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        log.info("用户注册");
        User u = userService.checkUser(user.getUsername());
        //1.用户已经存在
        if(u != null){//
            log.info("该用户已经注册");
            return new Result(2,"该用户已注册",null);
        } else if (user.getUsername() == null || user.getUsername() == "") {//2.用户注册失败（未输入用户名）
            return new Result(0,"用户注册失败，请输入用户名",null);
        } else if (user.getName() == null || user.getName() == "") {//用户注册失败
            return new Result(0,"用户注册失败，请输入姓名",null);
        } else{//3.用户注册成功
            userService.insertUser(user);
            return new Result(1,"注册成功",null);
        }
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        log.info("用户登录,{}",user);
        User u = userService.login(user);

        //登录成功，下发令牌
        if(u != null){
            Map<String,Object> claims = new HashMap<>();
            claims.put("id",u.getId());
            claims.put("name",u.getName());
            claims.put("username",u.getUsername());

            String jwt = JwtUtils.generateJwt(claims);//Jwt包含了当前登录的用户信息
            if (u.getId() == 1) {//管理员登录
                return new Result(2,"管理员登录",jwt);
            }else {//普通用户登录
                return Result.success(jwt);//(1,success,jwt)
            }
        }

        //登录失败，返回错误信息
        return Result.error("用户名或密码错误");//(0,msg,null)
    }
}
