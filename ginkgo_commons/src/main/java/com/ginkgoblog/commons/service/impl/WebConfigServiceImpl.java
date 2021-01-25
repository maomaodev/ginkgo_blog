package com.ginkgoblog.commons.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.base.constants.SqlConstants;
import com.ginkgoblog.base.enums.AccountTypeEnum;
import com.ginkgoblog.commons.entity.WebConfig;
import com.ginkgoblog.commons.feign.PictureFeignClient;
import com.ginkgoblog.commons.mapper.WebConfigMapper;
import com.ginkgoblog.commons.service.WebConfigService;
import com.ginkgoblog.commons.utils.WebUtils;
import com.ginkgoblog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 网站配置表 Service 层实现类
 *
 * @author maomao
 * @date 2021-01-25
 */
@Service
public class WebConfigServiceImpl extends ServiceImpl<WebConfigMapper, WebConfig> implements WebConfigService {

    @Autowired
    WebUtils webUtils;
    @Autowired
    private PictureFeignClient pictureFeignClient;

    @Override
    public WebConfig getWebConfigByShowList() {
        QueryWrapper<WebConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(SqlConstants.CREATE_TIME);
        WebConfig webConfig = this.getOne(queryWrapper);

        if (webConfig != null && StringUtils.isNotEmpty(webConfig.getLogo())) {
            String pictureList = pictureFeignClient.getPicture(webConfig.getLogo(), SqlConstants.FILE_SEGMENTATION);
            webConfig.setPhotoList(webUtils.getPicture(pictureList));
        }

        // 获取支付宝收款二维码
        if (StringUtils.isNotEmpty(webConfig.getAliPay())) {
            String pictureList = pictureFeignClient.getPicture(webConfig.getAliPay(), SqlConstants.FILE_SEGMENTATION);
            if (webUtils.getPicture(pictureList).size() > 0) {
                webConfig.setAliPayPhoto(webUtils.getPicture(pictureList).get(0));
            }
        }
        // 获取微信收款二维码
        if (StringUtils.isNotEmpty(webConfig.getWeixinPay())) {
            String pictureList = pictureFeignClient.getPicture(webConfig.getWeixinPay(), SqlConstants.FILE_SEGMENTATION);
            if (webUtils.getPicture(pictureList).size() > 0) {
                webConfig.setWeixinPayPhoto(webUtils.getPicture(pictureList).get(0));
            }
        }

        // 过滤一些不需要显示的用户账号信息
        String showListJson = webConfig.getShowList();

        String email = webConfig.getEmail();
        webConfig.setEmail("");
        String qqNumber = webConfig.getQqNumber();
        webConfig.setQqNumber("");
        String qqGroup = webConfig.getQqGroup();
        webConfig.setQqGroup("");
        String github = webConfig.getGithub();
        webConfig.setGithub("");
        String gitee = webConfig.getGitee();
        webConfig.setGitee("");
        String weChat = webConfig.getWeChat();
        webConfig.setWeChat("");

        // 根据配置重新设置需要显示的内容
        List<String> showList = JSON.parseArray(showListJson, String.class);

        for (String item : showList) {
            if (AccountTypeEnum.EMail.getCode().equals(item)) {
                webConfig.setEmail(email);
            }
            if (AccountTypeEnum.QQNumber.getCode().equals(item)) {
                webConfig.setQqNumber(qqNumber);
            }
            if (AccountTypeEnum.QQGroup.getCode().equals(item)) {
                webConfig.setQqGroup(qqGroup);
            }
            if (AccountTypeEnum.Github.getCode().equals(item)) {
                webConfig.setGithub(github);
            }
            if (AccountTypeEnum.Gitee.getCode().equals(item)) {
                webConfig.setGitee(gitee);
            }
            if (AccountTypeEnum.WeChat.getCode().equals(item)) {
                webConfig.setWeChat(weChat);
            }
        }
        return webConfig;
    }
}
