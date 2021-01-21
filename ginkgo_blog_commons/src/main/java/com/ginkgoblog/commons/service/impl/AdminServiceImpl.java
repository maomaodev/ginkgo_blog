package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.commons.entity.Admin;
import com.ginkgoblog.commons.mapper.AdminMapper;
import com.ginkgoblog.commons.service.AdminService;
import org.springframework.stereotype.Service;

/**
 * 管理员表 Service 层实现类
 *
 * @author maomao
 * @date 2021-01-13
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Override
    public void register(Admin admin) {
        admin.insert();
    }
}
