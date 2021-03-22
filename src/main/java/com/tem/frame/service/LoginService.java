package com.tem.frame.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tem.frame.dao.UserDao;
import com.tem.frame.exception.GlobalException;
import com.tem.frame.exception.GlobalExceptionCode;
import com.tem.frame.pojo.po.User;
import com.tem.frame.pojo.vo.UserDetail;
import com.tem.frame.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * 授权业务层
 */
@Service
public class LoginService {


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

        User user = this.userDao.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        Optional<User> userOp = Optional.ofNullable(user);
        if (userOp.isPresent()) {

            if (password.equals(userOp.get().getPassword())) {
                //生成 token
                String jwt = JWTUtil.INSTANCE.generate(String.valueOf(userOp.get().getId()), userOp.get().getUsername(), username, this.jwtSign, this.jwtExpiration);
                //过期时间
                long expireTimestamp = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli() + jwtExpiration;
                LocalDateTime expireTime = LocalDateTime.ofEpochSecond(expireTimestamp / 1000, 0, ZoneOffset.ofHours(8));

                //返回体
                UserDetail userDetail = new UserDetail();
                userDetail.setUserId(userOp.get().getId());
                userDetail.setUsername(userOp.get().getUsername());
                userDetail.setToken(jwt);
                userDetail.setExpireTime(expireTime);
                return userDetail;
            } else {
                throw new GlobalException(GlobalExceptionCode.USER_PASSWORD_ERROR);
            }
        } else {
            throw new GlobalException(GlobalExceptionCode.USER_NOT_FOUNT);
        }
    }

}
