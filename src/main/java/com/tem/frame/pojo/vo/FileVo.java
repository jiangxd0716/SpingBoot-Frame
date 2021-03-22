package com.tem.frame.pojo.vo;

import lombok.Data;

/**
 * 文件上传之后的返回信息
 */
@Data
public class FileVo {

    /**
     * 文件服务器地址
     */
    private String domain;

    /**
     * 文件原名称
     */
    private String originalFilename;

    /**
     * 文件上传之后在文件服务器上的名称
     */
    private String fileName;

    /**
     * 文件扩展名
     */
    private String extensionName;

    /**
     * 文件可以访问的 url 地址
     */
    private String url;

    /**
     * 文件上传之后在文件服务器上的相对路径
     * 前面拼接上 domain 即可访问
     */
    private String path;

    /**
     * 数据库存储路径
     */
    private String absolutePath;

    /**
     * 文件名称
     */
    private String oriName;

    /**
     * 文件扩展名
     */
    private String extName;

}
