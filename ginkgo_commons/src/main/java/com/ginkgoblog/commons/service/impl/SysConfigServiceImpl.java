package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.commons.entity.SysConfig;
import com.ginkgoblog.commons.mapper.SysConfigMapper;
import com.ginkgoblog.commons.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 系统配置表 Service 层实现类
 *
 * @author maomao
 * @date 2021-01-23
 */
@Slf4j
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {
}
