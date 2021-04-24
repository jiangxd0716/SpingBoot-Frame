package com.tem.frame.common.config;

import cn.hutool.core.util.StrUtil;
import com.tem.frame.common.exception.GlobalException;
import com.tem.frame.common.exception.GlobalExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Jedis 连接池配置
 *
 * @author jiangxd
 */
@Slf4j
@Configuration
public class JedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Value("${spring.redis.timeout}")
    private int redisTimeout;


    /**
     * 初始化 JedisPool
     * 使用默认配置
     *
     * @return
     */
    @Bean
    public JedisPool getJedisPool() {
        try {
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            if (StrUtil.isBlank(this.redisPassword)) {
                return new JedisPool(jedisPoolConfig, this.redisHost, this.redisPort, this.redisTimeout);
            } else {
                return new JedisPool(jedisPoolConfig, this.redisHost, this.redisPort, this.redisTimeout, this.redisPassword);
            }
        } catch (Throwable cause) {
            log.error("Jedis Pool 初始化失败");
            cause.printStackTrace();
            throw new GlobalException(GlobalExceptionCode.JEDIS_POOL_INIT_FAIL);
        }

    }

}
