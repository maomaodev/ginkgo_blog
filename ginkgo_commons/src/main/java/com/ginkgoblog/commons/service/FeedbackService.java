package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.Feedback;
import com.ginkgoblog.commons.vo.FeedbackVO;

import java.util.List;

/**
 * 反馈表 Service 层
 *
 * @author maomao
 * @date 2021-01-29
 */

public interface FeedbackService extends IService<Feedback> {
    /**
     * 获取反馈列表
     *
     * @param feedbackVO
     * @return
     */
    IPage<Feedback> getPageList(FeedbackVO feedbackVO);

    /**
     * 新增反馈
     *
     * @param feedbackVO
     */
    String addFeedback(FeedbackVO feedbackVO);

    /**
     * 批量删除反馈
     *
     * @param feedbackVOList
     */
    String deleteBatchFeedback(List<FeedbackVO> feedbackVOList);
}
