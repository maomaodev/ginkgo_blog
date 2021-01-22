package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.Admin;

/**
 * 管理员表 Service 层
 *
 * @author maomao
 * @date 2021-01-12
 */
public interface AdminService extends IService<Admin> {

    /**
     * 注册管理员
     *
     * @param admin 管理员实体
     */
    void register(Admin admin);
}
