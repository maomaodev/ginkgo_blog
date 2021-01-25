package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.base.constants.SqlConstants;
import com.ginkgoblog.commons.entity.Admin;
import com.ginkgoblog.commons.feign.PictureFeignClient;
import com.ginkgoblog.commons.mapper.AdminMapper;
import com.ginkgoblog.commons.service.AdminService;
import com.ginkgoblog.commons.utils.WebUtils;
import com.ginkgoblog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 管理员表 Service 层实现类
 *
 * @author maomao
 * @date 2021-01-13
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    WebUtils webUtils;
    @Autowired
    private PictureFeignClient pictureFeignClient;

    @Override
    public Admin getAdminByUser(String userName) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SqlConstants.USER_NAME, userName);
        queryWrapper.last("LIMIT 1");
        // 清空密码，防止泄露
        Admin admin = this.getOne(queryWrapper);
        admin.setPassWord(null);
        // 获取图片
        if (StringUtils.isNotEmpty(admin.getAvatar())) {
            String pictureList = pictureFeignClient.getPicture(admin.getAvatar(), ",");
            admin.setPhotoList(webUtils.getPicture(pictureList));
        }
        Admin result = new Admin();
        result.setNickName(admin.getNickName());
        result.setOccupation(admin.getOccupation());
        result.setSummary(admin.getSummary());
        result.setAvatar(admin.getAvatar());
        result.setPhotoList(admin.getPhotoList());
        result.setPersonResume(admin.getPersonResume());
        return result;
    }
}
