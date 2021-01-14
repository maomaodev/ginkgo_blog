package com.ginkgoblog.commons.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * @author maomao
 * @date 2021-01-12
 */
@Data
public class SuperEntity {
    /**
     * 唯一ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 是否删除，0表示未删除，1表示删除
     */
    @TableLogic
    private Boolean isDelete;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
