package com.tem.frame.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tem.frame.dao.UserDao;
import com.tem.frame.exception.GlobalException;
import com.tem.frame.exception.GlobalExceptionCode;
import com.tem.frame.pojo.dto.UserRegisterDto;
import com.tem.frame.pojo.po.User;
import com.tem.frame.pojo.vo.UserDetail;
import com.tem.frame.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * 授权业务层
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    /**
     * JWT 的签名
     */
    @Value("${jwt.sign}")
    private String jwtSign;

    /**
     * JWT 的过期时间
     */
    @Value("${jwt.expiration}")
    private Long jwtExpiration;


    /**
     * 根据用户名获取 jwt 令牌
     *
     * @param username 用户名
     * @param password 密码
     */
    public UserDetail login(String username, String password) {
        //根据用户名获取用户
        User user = this.isExistUser(username);
        //若存在则进行校验密码
        //校验用户密码
        if (password.equals(user.getPassword())) {
            //生成 token
            String jwt = JWTUtil.INSTANCE.generate(String.valueOf(user.getId()), user.getUsername(), username, this.jwtSign, this.jwtExpiration);
            //过期时间
            long expireTimestamp = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli() + jwtExpiration;
            LocalDateTime expireTime = LocalDateTime.ofEpochSecond(expireTimestamp / 1000, 0, ZoneOffset.ofHours(8));

            //返回体
            UserDetail userDetail = new UserDetail();
            userDetail.setUserId(user.getId());
            userDetail.setUsername(user.getUsername());
            userDetail.setToken(jwt);
            userDetail.setExpireTime(expireTime);
            return userDetail;
        } else {
            throw new GlobalException(GlobalExceptionCode.USER_PASSWORD_ERROR);
        }
    }

    /**
     * 用户注册
     *
     * @param registerDto 注册用户信息
     */
    public void register(UserRegisterDto registerDto) {
        User newUser = new User();
        BeanUtil.copyProperties(registerDto, newUser);
        try {
            //数据库设置username字段为unique_index
            this.userDao.insert(newUser);
        } catch (DuplicateKeyException e) {
            throw new GlobalException(GlobalExceptionCode.USER_EXIST);
        }
    }

    /**
     * 根据用户名判断用户是否存在
     *
     * @param username 用户名
     * @return 用户信息
     */
    public User isExistUser(String username) {
        //根据用户名获取用户
        User user = this.userDao.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        Optional<User> userOp = Optional.ofNullable(user);
        if (userOp.isPresent()) {
            return userOp.get();
        } else {
            throw new GlobalException(GlobalExceptionCode.USER_NOT_FOUNT);
        }
    }

}
