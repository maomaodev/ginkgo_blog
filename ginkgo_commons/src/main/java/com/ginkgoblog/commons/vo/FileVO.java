package com.ginkgoblog.commons.vo;

import com.ginkgoblog.base.vo.SuperVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 文件表 表现层对象
 *
 * @author maomao
 * @date 2021-01-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileVO extends SuperVO {
    /**
     * 如果是用户上传，则包含用户uid
     */
    private String userUid;

    /**
     * 如果是管理员上传，则包含管理员uid
     */
    private String adminUid;

    /**
     * 项目名
     */
    private String projectName;

    /**
     * 模块名
     */
    private String sortName;

    /**
     * 图片Url集合
     */
    private List<String> urlList;

    /**
     * 系统配置
     */
    private Map<String, Object> systemConfig;
}
