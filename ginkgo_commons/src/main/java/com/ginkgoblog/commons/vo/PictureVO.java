package com.ginkgoblog.commons.vo;

import com.ginkgoblog.base.vo.SuperVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图片表 表现层对象
 *
 * @author maomao
 * @date 2021-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PictureVO extends SuperVO {
    /**
     * 图片UID
     */
    private String fileUid;

    /**
     * 图片UIDs
     */
//    @NotBlank(groups = {Insert.class})
    private String fileUids;

    /**
     * 图片名称
     */
    private String picName;

    /**
     * 所属相册分类UID
     */
//    @NotBlank(groups = {Insert.class, Update.class, GetList.class})
    private String pictureSortUid;
}
