package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.commons.entity.Role;
import com.ginkgoblog.commons.mapper.RoleMapper;
import com.ginkgoblog.commons.service.RoleService;
import org.springframework.stereotype.Service;

/**
 * 角色表 Service 层实现类
 *
 * @author maomao
 * @date 2021-01-25
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}
