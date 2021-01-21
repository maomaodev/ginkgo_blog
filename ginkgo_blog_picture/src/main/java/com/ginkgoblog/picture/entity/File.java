package com.ginkgoblog.picture.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ginkgoblog.base.entity.SuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件表
 *
 * @author maomao
 * @date 2021-01-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_file")
public class File extends SuperEntity {
    /**
     * 旧图片名
     */
    private String fileOldName;

    /**
     * 图片大小
     */
    private Long fileSize;

    /**
     * 图片扩展名
     */
    private String picExpandedName;

    /**
     * 图片名称
     */
    private String picName;

    /**
     * 图片url地址
     */
    private String picUrl;

    /**
     * 图片分类uid
     */
    private String fileSortUid;

    /**
     * 管理员Uid
     */
    private String adminUid;

    /**
     * 用户Uid
     */
    private String userUid;

    /**
     * 七牛云Url
     */
    private String qiNiuUrl;
}
