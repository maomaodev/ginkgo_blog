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
     * Web端通过用户名获取一个Admin
     *
     * @param userName
     * @return
     */
    Admin getAdminByUser(String userName);
}
