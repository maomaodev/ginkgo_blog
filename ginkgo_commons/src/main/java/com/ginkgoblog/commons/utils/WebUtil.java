package com.ginkgoblog.commons.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ginkgoblog.base.enums.EOpenStatus;
import com.ginkgoblog.commons.constants.SysConf;
import com.ginkgoblog.commons.entity.SysConfig;
import com.ginkgoblog.commons.service.SysConfigService;
import com.ginkgoblog.utils.JsonUtils;
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
public class WebUtil {

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
        Map<String, Object> picMap = (Map<String, Object>) JsonUtils.jsonToObject(result, Map.class);
        if (SysConf.SUCCESS.equals(picMap.get(SysConf.CODE))) {
            List<Map<String, Object>> picData = (List<Map<String, Object>>) picMap.get(SysConf.DATA);
            if (picData.size() > 0) {
                for (int i = 0; i < picData.size(); i++) {
                    if ("1".equals(picturePriority)) {
                        picUrls.add(qiNiuPictureBaseUrl + picData.get(i).get(SysConf.QI_NIU_URL));
                    } else {
                        picUrls.add(localPictureBaseUrl + picData.get(i).get(SysConf.URL));
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
        SysConfig sysConfig = sysConfigService.getOne(queryWrapper);
        String picturePriority = sysConfig.getPicturePriority();
        String localPictureBaseUrl = sysConfig.getLocalPictureBaseUrl();
        String qiNiuPictureBaseUrl = sysConfig.getQiNiuPictureBaseUrl();

        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> picMap = (Map<String, Object>) JsonUtils.jsonToObject(result, Map.class);
        if (SysConf.SUCCESS.equals(picMap.get(SysConf.CODE))) {
            List<Map<String, Object>> picData = (List<Map<String, Object>>) picMap.get(SysConf.DATA);
            if (picData.size() > 0) {
                for (int i = 0; i < picData.size(); i++) {
                    Map<String, Object> map = new HashMap<>();
                    if (StringUtils.isEmpty(picData.get(i).get(SysConf.URL)) || StringUtils.isEmpty(picData.get(i).get(SysConf.UID))) {
                        continue;
                    }
                    // 图片优先显示 七牛云 or 本地
                    if (EOpenStatus.OPEN.equals(picturePriority)) {
                        map.put(SysConf.URL, qiNiuPictureBaseUrl + picData.get(i).get(SysConf.QI_NIU_URL));
                    } else {
                        map.put(SysConf.URL, localPictureBaseUrl + picData.get(i).get(SysConf.URL));
                    }
                    map.put(SysConf.UID, picData.get(i).get(SysConf.UID));
                    resultList.add(map);
                }
            }
        }
        return resultList;
    }
}
