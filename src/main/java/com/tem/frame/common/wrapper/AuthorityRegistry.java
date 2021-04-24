package com.tem.frame.common.wrapper;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存接口权限
 * 单例
 */
@Slf4j
@ToString
public enum AuthorityRegistry {

    INSTANCE;


    /**
     * 一个线程安全的注册器
     * 结构
     * app  object
     * method:url     authority
     */
    private final ConcurrentHashMap<String, AuthorityWrapper> registry = new ConcurrentHashMap<>(16, 0.75f, 4);

    /**
     * 将接口url和权限标识缓存
     *
     * @param methodType       请求类型  如 : GET , POST
     * @param path             接口路径
     * @param authorityWrapper 权限标识
     */
    public void put(String methodType, String path, AuthorityWrapper authorityWrapper) {
        registry.put(String.format("%s:%s", methodType, path), authorityWrapper);
        log.info("缓存接口权限标识[{}]成功", authorityWrapper);
    }

    /**
     * 根据请求类型和请求路径获取权限标识
     *
     * @param methodType 请求类型  如 : GET , POST
     * @param path       请求路径 , 注意该路径应为不带微服务标识的路径 , 如在浏览器中呈现的为 http://***:80/user/login , 那么此处参数应为 /login
     * @return 若存在则返回当前请求所对应的权限对象 , 否则返回 null
     */
    public AuthorityWrapper get(String methodType, String path) {
        return registry.get(String.format("%s:%s", methodType, path));
    }

}
