package com.ginkgoblog.commons.vo;

import com.ginkgoblog.base.vo.SuperVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图片分类表 表现层对象
 *
 * @author maomao
 * @date 2021-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PictureSortVO extends SuperVO {
    /**
     * 父UID
     */
    private String parentUid;

    /**
     * 分类名
     */
    private String name;

    /**
     * 分类图片Uid
     */
    private String fileUid;

    /**
     * 排序字段，数值越大，越靠前
     */
    private int sort;

    /**
     * 是否显示  1: 是  0: 否
     */
//    @IntegerNotNull(groups = {Insert.class, Update.class})
    private Integer isShow;
}
