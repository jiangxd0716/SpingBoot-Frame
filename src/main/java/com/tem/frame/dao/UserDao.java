package com.tem.frame.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tem.frame.pojo.po.User;
import org.springframework.stereotype.Repository;

/**
 * 用户 dao
 */
@Repository
public interface UserDao extends BaseMapper<User> {

}
