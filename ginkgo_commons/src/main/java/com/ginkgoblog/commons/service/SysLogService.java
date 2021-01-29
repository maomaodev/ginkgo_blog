package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.SysLog;
import com.ginkgoblog.commons.vo.SysLogVO;

/**
 * 系统日志表 Service 层
 *
 * @author maomao
 * @date 2021-01-17
 */
public interface SysLogService extends IService<SysLog> {
    /**
     * 获取操作日志列表
     *
     * @param sysLogVO
     * @return
     */
    IPage<SysLog> getPageList(SysLogVO sysLogVO);
}
