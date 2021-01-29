package com.ginkgoblog.commons.vo;

import com.ginkgoblog.base.vo.SuperVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author maomao
 * @date 2021-01-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FeedbackVO extends SuperVO {
    /**
     * 用户uid
     */
    private String userUid;

    /**
     * 标题
     */
//    @NotBlank(groups = {Insert.class, Update.class})
    private String title;

    /**
     * 反馈的内容
     */
//    @NotBlank(groups = {Insert.class, Update.class})
    private String content;

    /**
     * 回复
     */
    private String reply;

    /**
     * 反馈状态： 0：已开启  1：进行中  2：已完成  3：已拒绝
     */
    private Integer feedbackStatus;
}
