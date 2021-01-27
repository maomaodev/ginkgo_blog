package com.ginkgoblog.picture.utils;

import com.alibaba.fastjson.JSON;
import com.ginkgoblog.base.constants.SqlConstants;
import com.ginkgoblog.base.enums.EQiNiuArea;
import com.ginkgoblog.utils.StringUtils;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Map;

/**
 * 七牛云工具类
 *
 * @author maomao
 * @date 2021-01-21
 */
@Slf4j
public class QiniuUtil {

    /**
     * 七牛云上传图片
     *
     * @param localFilePath
     * @return
     */
    public String uploadQiniu(File localFilePath, Map<String, String> qiNiuConfig) throws QiniuException {

        // 构造一个带指定Zone对象的配置类
        Configuration cfg = setQiNiuArea(qiNiuConfig);
        // 生成上传凭证，然后准备上传
        String accessKey = qiNiuConfig.get(SqlConstants.QI_NIU_ACCESS_KEY);
        String secretKey = qiNiuConfig.get(SqlConstants.QI_NIU_SECRET_KEY);
        String bucket = qiNiuConfig.get(SqlConstants.QI_NIU_BUCKET);
        // 其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        String key = StringUtils.getUUID();
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        Response response = uploadManager.put(localFilePath, key, upToken);
        // 解析上传成功的结果
        DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
        log.info("{七牛图片上传key: " + putRet.key + ",七牛图片上传hash: " + putRet.hash + "}");
        return putRet.key;
    }

    /**
     * 设置七牛云上传区域（内部方法）
     *
     * @param qiNiuConfig
     * @return
     */
    private Configuration setQiNiuArea(Map<String, String> qiNiuConfig) {
        // 生成上传凭证，然后准备上传
        String area = qiNiuConfig.get("qiNiuArea");

        // 构造一个带指定Zone对象的配置类
        Configuration cfg = null;

        //zong2() 代表华南地区
        switch (EQiNiuArea.valueOf(area).getCode()) {
            case "z0":
                cfg = new Configuration(Zone.zone0());
                break;
            case "z1":
                cfg = new Configuration(Zone.zone1());
                break;
            case "z2":
                cfg = new Configuration(Zone.zone2());
                break;
            case "na0":
                cfg = new Configuration(Zone.zoneNa0());
                break;
            case "as0":
                cfg = new Configuration(Zone.zoneAs0());
                break;
            default:
                break;
        }
        return cfg;
    }
}
