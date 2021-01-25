package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.WebConfig;

/**
 * 网站配置表 Service 层
 *
 * @author maomao
 * @date 2021-01-25
 */
public interface WebConfigService extends IService<WebConfig> {

    /**
     * 通过显示列表获取配置
     *
     * @return
     */
    WebConfig getWebConfigByShowList();
}
