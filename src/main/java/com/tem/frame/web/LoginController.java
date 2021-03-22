package com.tem.frame.web;

import com.tem.frame.pojo.vo.UserDetail;
import com.tem.frame.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 授权 Controller
 */
@Slf4j
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;


    /**
     * 根据用户名和密码获取用户的 JWT 令牌
     *
     * @param username 用户名
     * @param password 密码
     */
    @GetMapping()
    public UserDetail login(@RequestParam("username") String username,
                            @RequestParam(value = "password", required = false) String password) {
        log.info("获取到登录请求[{}:{}]", username, password);
        return this.loginService.login(username, password);
    }

}
