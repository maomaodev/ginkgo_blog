package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.commons.entity.SysLog;
import com.ginkgoblog.commons.mapper.SysLogMapper;
import com.ginkgoblog.commons.service.SysLogService;
import org.springframework.stereotype.Service;

/**
 * 系统日志表 Service 层实现类
 *
 * @author maomao
 * @date 2021-01-25
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {
}
