package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.SysConfig;
import com.ginkgoblog.commons.entity.SysLog;
import com.ginkgoblog.commons.vo.SysConfigVO;
import com.ginkgoblog.commons.vo.SysLogVO;

import java.util.List;

/**
 * 系统配置表 Service 层
 *
 * @author maomao
 * @date 2021-01-23
 */
public interface SysConfigService extends IService<SysConfig> {
    /**
     * 获取系统配置
     *
     * @return
     */
     SysConfig getConfig();

    /**
     * 通过Key前缀清空Redis缓存
     *
     * @return
     */
     String cleanRedisByKey(List<String> key);

    /**
     * 修改系统配置
     *
     * @return
     */
     String editSystemConfig(SysConfigVO sysConfigVO);
}
