package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.WebConfig;
import com.ginkgoblog.commons.vo.WebConfigVO;

/**
 * 网站配置表 Service 层
 *
 * @author maomao
 * @date 2021-01-25
 */
public interface WebConfigService extends IService<WebConfig> {

    /**
     * 获取网站配置
     *
     * @return
     */
     WebConfig getWebConfig();

    /**
     * 通过显示列表获取配置
     *
     * @return
     */
     WebConfig getWebConfigByShowList();

    /**
     * 修改网站配置
     *
     * @param webConfigVO
     * @return
     */
     String editWebConfig(WebConfigVO webConfigVO);
}
