package com.ginkgoblog.commons.utils;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ginkgoblog.base.constants.SqlConstants;
import com.ginkgoblog.base.constants.SystemConstants;
import com.ginkgoblog.base.enums.OpenStatusEnum;
import com.ginkgoblog.commons.entity.SysConfig;
import com.ginkgoblog.commons.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * web有关的工具类
 *
 * @author maomao
 * @date 2021-01-24
 */
@Component
public class WebUtils {

    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 格式化数据获取图片列表
     *
     * @param result
     * @return
     */
    public List<String> getPicture(String result) {

        QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<>();
        SysConfig sysConfig = sysConfigService.getOne(queryWrapper);
        String picturePriority = sysConfig.getPicturePriority();
        String localPictureBaseUrl = sysConfig.getLocalPictureBaseUrl();
        String qiNiuPictureBaseUrl = sysConfig.getQiNiuPictureBaseUrl();

        List<String> picUrls = new ArrayList<>();
        Map<String, Object> picMap = (Map<String, Object>) JSON.parse(result);
        if (SystemConstants.SUCCESS.equals(picMap.get(SystemConstants.CODE))) {
            List<Map<String, Object>> picData = (List<Map<String, Object>>) picMap.get(SystemConstants.DATA);
            if (picData.size() > 0) {
                for (int i = 0; i < picData.size(); i++) {
                    if ("1".equals(picturePriority)) {
                        picUrls.add(qiNiuPictureBaseUrl + picData.get(i).get(SqlConstants.QI_NIU_URL));
                    } else {
                        picUrls.add(localPictureBaseUrl + picData.get(i).get(SqlConstants.URL));
                    }
                }
            }
        }
        return picUrls;
    }

    /**
     * 获取图片，返回Map
     *
     * @param result
     * @return
     */
    public List<Map<String, Object>> getPictureMap(String result) {

        QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<>();
        SysConfig systemConfig = sysConfigService.getOne(queryWrapper);
        String picturePriority = systemConfig.getPicturePriority();
        String localPictureBaseUrl = systemConfig.getLocalPictureBaseUrl();
        String qiNiuPictureBaseUrl = systemConfig.getQiNiuPictureBaseUrl();

        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> picMap = (Map<String, Object>) JSON.parse(result);

        if (SystemConstants.SUCCESS.equals(picMap.get(SystemConstants.CODE))) {
            List<Map<String, Object>> picData = (List<Map<String, Object>>) picMap.get(SystemConstants.DATA);
            if (picData.size() > 0) {
                for (int i = 0; i < picData.size(); i++) {
                    Map<String, Object> map = new HashMap<>();
                    if (org.springframework.util.StringUtils.isEmpty(picData.get(i).get(SqlConstants.URL))
                            || StringUtils.isEmpty(picData.get(i).get(SqlConstants.UID))) {
                        continue;
                    }
                    // 图片优先显示 七牛云 or 本地
                    if (OpenStatusEnum.OPEN.equals(picturePriority)) {
                        map.put(SqlConstants.URL, qiNiuPictureBaseUrl + picData.get(i).get(SqlConstants.QI_NIU_URL));
                    } else {
                        map.put(SqlConstants.URL, localPictureBaseUrl + picData.get(i).get(SqlConstants.URL));
                    }
                    map.put(SqlConstants.UID, picData.get(i).get(SqlConstants.UID));
                    resultList.add(map);
                }
            }
        }
        return resultList;
    }
}
