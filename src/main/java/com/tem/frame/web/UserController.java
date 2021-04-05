package com.tem.frame.web;

import com.tem.frame.pojo.dto.UserRegisterDto;
import com.tem.frame.pojo.vo.UserDetail;
import com.tem.frame.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 授权 Controller
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * 根据用户名和密码登录
     * 获取用户的 JWT 令牌
     *
     * @param username 用户名
     * @param password 密码
     */
    @GetMapping("/login")
    public UserDetail login(@RequestParam("username") String username,
                            @RequestParam(value = "password", required = false) String password) {
        log.info("获取到登录请求[{}:{}]", username, password);
        return this.userService.login(username, password);
    }

    /**
     * 用户注册
     *
     * @param registerDto 注册参数
     */
    @PutMapping("register")
    public void register(@RequestBody @Valid UserRegisterDto registerDto) {
        log.info("获取到注册请求[{}:{}]", registerDto.getUsername(), registerDto.getPassword());
        this.userService.register(registerDto);
    }

}
