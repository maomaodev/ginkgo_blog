package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.base.enums.EOpenStatus;
import com.ginkgoblog.base.enums.EStatus;
import com.ginkgoblog.commons.constants.MessageConf;
import com.ginkgoblog.commons.constants.RedisConf;
import com.ginkgoblog.commons.constants.SQLConf;
import com.ginkgoblog.commons.constants.SysConf;
import com.ginkgoblog.commons.entity.SysConfig;
import com.ginkgoblog.commons.mapper.SysConfigMapper;
import com.ginkgoblog.commons.service.SysConfigService;
import com.ginkgoblog.commons.vo.SysConfigVO;
import com.ginkgoblog.utils.RedisUtil;
import com.ginkgoblog.utils.ResultUtil;
import com.ginkgoblog.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 系统配置表 Service 层实现类
 *
 * @author maomao
 * @date 2021-01-23
 */
@Slf4j
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {
    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public SysConfig getConfig() {
        QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.last("LIMIT 1");
        SysConfig SysConfig = sysConfigService.getOne(queryWrapper);
        return SysConfig;
    }

    @Override
    public String cleanRedisByKey(List<String> key) {
        if (key == null) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.OPERATION_FAIL);
        }

        key.forEach(item -> {
            // 表示清空所有key
            if (RedisConf.ALL.equals(item)) {
                Set<String> keys = redisUtil.keys("*");
                redisUtil.delete(keys);
            } else {
                // 获取Redis中特定前缀
                Set<String> keys = redisUtil.keys(key + "*");
                redisUtil.delete(keys);
            }
        });
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public String editSystemConfig(SysConfigVO sysConfigVO) {
        if (EOpenStatus.CLOSE.equals(sysConfigVO.getUploadLocal()) && EOpenStatus.CLOSE.equals(sysConfigVO.getUploadQiNiu())) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.PICTURE_MUST_BE_SELECT_AREA);
        }

        if (EOpenStatus.CLOSE.equals(sysConfigVO.getPicturePriority()) && EOpenStatus.CLOSE.equals(sysConfigVO.getUploadLocal())) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.MUST_BE_OPEN_LOCAL_UPLOAD);
        }

        if (EOpenStatus.OPEN.equals(sysConfigVO.getPicturePriority()) && EOpenStatus.CLOSE.equals(sysConfigVO.getUploadQiNiu())) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.MUST_BE_OPEN_QI_NIU_UPLOAD);
        }

        // 开启Email邮件通知时，必须保证Email字段不为空
        if (EOpenStatus.OPEN.equals(sysConfigVO.getStartEmailNotification()) && StringUtils.isEmpty(sysConfigVO.getEmail())) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.MUST_BE_SET_EMAIL);
        }

        if (StringUtils.isEmpty(sysConfigVO.getUid())) {

            SysConfig SysConfig = new SysConfig();

            // 设置七牛云相关
            SysConfig.setLocalPictureBaseUrl(sysConfigVO.getLocalPictureBaseUrl());
            SysConfig.setQiNiuPictureBaseUrl(sysConfigVO.getQiNiuPictureBaseUrl());
            SysConfig.setQiNiuAccessKey(sysConfigVO.getQiNiuAccessKey());
            SysConfig.setQiNiuSecretKey(sysConfigVO.getQiNiuSecretKey());
            SysConfig.setQiNiuBucket(sysConfigVO.getQiNiuBucket());
            SysConfig.setQiNiuArea(sysConfigVO.getQiNiuArea());
            SysConfig.setUploadLocal(sysConfigVO.getUploadLocal());
            SysConfig.setUploadQiNiu(sysConfigVO.getUploadQiNiu());
            SysConfig.setPicturePriority(sysConfigVO.getPicturePriority());

            // 设置邮箱相关
            SysConfig.setEmail(sysConfigVO.getEmail());
            SysConfig.setEmailPassword(sysConfigVO.getEmailPassword());
            SysConfig.setEmailUserName(sysConfigVO.getEmailUserName());
            SysConfig.setSmtpAddress(sysConfigVO.getSmtpAddress());
            SysConfig.setSmtpPort(sysConfigVO.getSmtpPort());
            SysConfig.setStartEmailNotification(sysConfigVO.getStartEmailNotification());
            SysConfig.insert();
        } else {

            SysConfig SysConfig = sysConfigService.getById(sysConfigVO.getUid());
            // 设置七牛云相关
            SysConfig.setLocalPictureBaseUrl(sysConfigVO.getLocalPictureBaseUrl());
            SysConfig.setQiNiuPictureBaseUrl(sysConfigVO.getQiNiuPictureBaseUrl());
            SysConfig.setQiNiuAccessKey(sysConfigVO.getQiNiuAccessKey());
            SysConfig.setQiNiuSecretKey(sysConfigVO.getQiNiuSecretKey());
            SysConfig.setQiNiuBucket(sysConfigVO.getQiNiuBucket());
            SysConfig.setQiNiuArea(sysConfigVO.getQiNiuArea());
            SysConfig.setUploadLocal(sysConfigVO.getUploadLocal());
            SysConfig.setUploadQiNiu(sysConfigVO.getUploadQiNiu());
            SysConfig.setPicturePriority(sysConfigVO.getPicturePriority());

            // 设置邮箱相关
            SysConfig.setEmail(sysConfigVO.getEmail());
            SysConfig.setEmailPassword(sysConfigVO.getEmailPassword());
            SysConfig.setEmailUserName(sysConfigVO.getEmailUserName());
            SysConfig.setSmtpAddress(sysConfigVO.getSmtpAddress());
            SysConfig.setSmtpPort(sysConfigVO.getSmtpPort());
            SysConfig.setStartEmailNotification(sysConfigVO.getStartEmailNotification());
            SysConfig.setUpdateTime(new Date());
            SysConfig.updateById();

        }

        // 更新系统配置成功后，需要删除Redis中的系统配置，主要用于mogu_picture获取上传配置信息
        ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attribute.getRequest();
        if(request.getAttribute(SysConf.TOKEN) != null) {
            String token = request.getAttribute(SysConf.TOKEN).toString();
            redisUtil.delete(RedisConf.SYSTEM_CONFIG + RedisConf.SEGMENTATION + token);
            log.info("成功删除Redis中的系统配置！");
        }
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.UPDATE_SUCCESS);
    }
}
