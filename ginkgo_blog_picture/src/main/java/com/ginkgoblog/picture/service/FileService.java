package com.ginkgoblog.picture.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.picture.entity.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 文件表 Service 层
 *
 * @author maomao
 * @date 2021-01-21
 */
public interface FileService extends IService<File> {
    /**
     * 多文件上传
     *
     * @param path        上次路径
     * @param files       文件数组
     * @param qiNiuConfig 七牛云配置
     * @return 处理信息
     */
    String uploadImages(String path, List<MultipartFile> files, Map<String, String> qiNiuConfig);
}
