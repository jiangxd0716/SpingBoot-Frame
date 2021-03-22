package com.tem.frame.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.function.Function;

/**
 * Jedis 客户端
 */
@Component
public class JedisClient {


    @Autowired
    private JedisPool jedisPool;


    /**
     * Jedis 保存
     *
     * @param function
     * @param <R>
     * @return
     */
    public <R> R operate(Function<Jedis, R> function) {
        Jedis jedis = this.jedisPool.getResource();
        try {

            R result = function.apply(jedis);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return null;
    }

    /**
     * 获取一个 Redis 的链接
     *
     * @return
     */
    public Jedis getJedis() {
        return this.jedisPool.getResource();
    }


}
