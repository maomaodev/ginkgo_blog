package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.Admin;
import com.ginkgoblog.commons.vo.AdminVO;

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


    /**
     * 获取当前管理员
     *
     * @return
     */
    Admin getMe();

    /**
     * 添加在线用户
     * @param admin
     * @return
     */
    void addOnlineAdmin(Admin admin);

    /**
     * 编辑当前管理员信息
     *
     * @return
     */
    String editMe(AdminVO adminVO);

    /**
     * 修改密码
     *
     * @return
     */
    String changePwd(String oldPwd, String newPwd);
}
