package com.tem.frame.common.wrapper;

import lombok.Builder;
import lombok.Data;

/**
 * 用于封装一个请求的基本信息
 *
 * @author jiangxd
 */
@Data
@Builder
public class AuthorityWrapper {

    /**
     * 请求地址
     */
    private String path;

    /**
     * 类型 , 如 GET POST PUT 等
     */
    private String method;

    /**
     * 权限标识 , 若两个权限同时拥有一个接口则以逗号分隔
     */
    private String mark;

    /**
     * 该权限的描述信息
     */
    private String name;

}
