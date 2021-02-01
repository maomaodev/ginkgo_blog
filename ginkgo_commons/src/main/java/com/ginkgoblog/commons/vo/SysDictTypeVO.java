package com.ginkgoblog.commons.vo;

import com.ginkgoblog.base.validator.annotion.IntegerNotNull;
import com.ginkgoblog.base.vo.SuperVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型表 表现层对象
 *
 * @author maomao
 * @date 2021-02-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDictTypeVO extends SuperVO {
    /**
     * 自增键 oid
     */
    private Long oid;

    /**
     * 字典名称
     */
//    @NotBlank(groups = {Insert.class, Update.class})
    private String dictName;

    /**
     * 字典类型
     */
//    @NotBlank(groups = {Insert.class, Update.class})
    private String dictType;

    /**
     * 是否发布  1：是，0:否，默认为0
     */
//    @NotBlank(groups = {Insert.class, Update.class})
    private String isPublish;

    /**
     * 备注
     */
    private String remark;

    /**
     * 排序字段
     */
//    @IntegerNotNull(groups = {Insert.class, Update.class})
    private Integer sort;
}
