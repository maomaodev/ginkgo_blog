package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.ExceptionLog;
import com.ginkgoblog.commons.vo.ExceptionLogVO;

/**
 * 异常日志表 Service 层
 *
 * @author maomao
 * @date 2021-01-17
 */
public interface ExceptionLogService extends IService<ExceptionLog> {
    /**
     * 获取异常日志列表
     *
     * @param exceptionLogVO
     * @return
     */
    IPage<ExceptionLog> getPageList(ExceptionLogVO exceptionLogVO);
}
