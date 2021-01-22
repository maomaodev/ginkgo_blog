package com.ginkgoblog.picture.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ginkgoblog.base.entity.SuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件分类表
 *
 * @author maomao
 * @date 2021-01-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_file_sort")
public class FileSort extends SuperEntity {
    /**
     * 项目名
     */
    private String projectName;

    /**
     * 分类名
     */
    private String sortName;

    /**
     * 分类路径
     */
    private String url;
}
