package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.Role;
import com.ginkgoblog.commons.vo.RoleVO;

/**
 * 角色表 Service 层
 *
 * @author maomao
 * @date 2021-01-17
 */
public interface RoleService extends IService<Role> {
    /**
     * 获取角色列表
     *
     * @param roleVO
     * @return
     */
    IPage<Role> getPageList(RoleVO roleVO);

    /**
     * 新增角色
     *
     * @param roleVO
     */
    String addRole(RoleVO roleVO);

    /**
     * 编辑角色
     *
     * @param roleVO
     */
    String editRole(RoleVO roleVO);

    /**
     * 删除角色
     *
     * @param roleVO
     */
    String deleteRole(RoleVO roleVO);
}
