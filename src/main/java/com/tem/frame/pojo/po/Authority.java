package com.tem.frame.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 权限表
 *
 * @author jiangxd
 */
@Data
@TableName("t_authority")
public class Authority {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    /**
     * 权限名称
     */
    @TableField("name")
    private String name;

    /**
     * 权限标识
     */
    @TableField("mark")
    private String mark;

}
