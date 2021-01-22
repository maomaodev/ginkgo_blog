package com.ginkgoblog.picture.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.base.constants.MessageConstants;
import com.ginkgoblog.base.constants.SqlConstants;
import com.ginkgoblog.base.constants.SystemConstants;
import com.ginkgoblog.base.enums.StatusEnum;
import com.ginkgoblog.base.holder.RequestHolder;
import com.ginkgoblog.picture.entity.File;
import com.ginkgoblog.picture.entity.FileSort;
import com.ginkgoblog.picture.mapper.FileMapper;
import com.ginkgoblog.picture.service.FileService;
import com.ginkgoblog.picture.service.FileSortService;
import com.ginkgoblog.picture.utils.QiniuUtil;
import com.ginkgoblog.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 文件表 Service 层实现类
 *
 * @author maomao
 * @date 2021-01-21
 */
@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    @Autowired
    private FileSortService fileSortService;

    @Override
    public String uploadImages(String path, HttpServletRequest request, List<MultipartFile> files, Map<String, String> qiNiuConfig) {
        // 获取来源
        String source = request.getParameter(SqlConstants.SOURCE);

        // 如果是用户上传，则包含用户uid
        String userUid = "";
        // 如果是管理员上传，则包含管理员uid
        String adminUid = "";
        // 项目名
        String projectName = "";
        // 模块名
        String sortName = "";

        // 判断图片来源
        if (SqlConstants.PICTURE.equals(source)) {
            userUid = request.getParameter(SqlConstants.USER_UID);
            adminUid = request.getParameter(SqlConstants.ADMIN_UID);
            projectName = request.getParameter(SqlConstants.PROJECT_NAME);
            sortName = request.getParameter(SqlConstants.SORT_NAME);
        } else {
            userUid = request.getAttribute(SqlConstants.USER_UID).toString();
            adminUid = request.getAttribute(SqlConstants.ADMIN_UID).toString();
            projectName = request.getAttribute(SqlConstants.PROJECT_NAME).toString();
            sortName = request.getAttribute(SqlConstants.SORT_NAME).toString();
        }

        // projectName现在默认base
        if (StringUtils.isEmpty(projectName)) {
            projectName = "base";
        }

        // 这里可以检测用户上传，如果不是网站的用户就不能调用
        if (StringUtils.isEmpty(userUid) && StringUtils.isEmpty(adminUid)) {
            return ResultUtils.result(SystemConstants.ERROR, "请先注册");
        }

        QueryWrapper<FileSort> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SqlConstants.SORT_NAME, sortName)
                .eq(SqlConstants.PROJECT_NAME, projectName)
                .eq(SqlConstants.STATUS, StatusEnum.ENABLE);
        List<FileSort> fileSorts = fileSortService.list(queryWrapper);

        FileSort fileSort;
        if (fileSorts.size() >= 1) {
            fileSort = fileSorts.get(0);
            log.info("====fileSort====" + JSON.toJSONString(fileSort));
        } else {
            return ResultUtils.result(SystemConstants.ERROR, "文件不被允许上传");
        }

        String sortUrl = fileSort.getUrl();
        // 判断url是否为空，如果为空，使用默认
        if (StringUtils.isEmpty(sortUrl)) {
            sortUrl = "base/common/";
        }

        List<File> lists = new ArrayList<>();
        if (files != null && files.size() > 0) {
            for (MultipartFile file : files) {
                // 获取文件名（包括后缀名）、大小
                String oldName = file.getOriginalFilename();
                long fileSize = file.getSize();
                log.info("上传文件====：" + oldName);
                log.info("文件大小====：" + fileSize);
                // 获取扩展名，默认是jpg
                String picExpandedName = FileUtils.getPicExpandedName(oldName);

                // 获取新文件名
                String newFileName = System.currentTimeMillis() + "." + picExpandedName;
                log.info(newFileName + ":" + oldName);
                // 文件路径问题
                String newPath = path + sortUrl + "/" + picExpandedName + "/" + DateUtils.getYears() + "/"
                        + DateUtils.getMonth() + "/" + DateUtils.getDay() + "/";
                String picurl = sortUrl + "/" + picExpandedName + "/" + DateUtils.getYears() + "/"
                        + DateUtils.getMonth() + "/" + DateUtils.getDay() + "/" + newFileName;
                log.info("newPath====" + newPath);
                String saveUrl = newPath + newFileName;

                String uploadQiNiu = qiNiuConfig.get(SqlConstants.UPLOAD_QI_NIU);
                String uploadLocal = qiNiuConfig.get(SqlConstants.UPLOAD_LOCAL);
                BufferedOutputStream out = null;
                QiniuUtil qn = new QiniuUtil();
                String qiNiuUrl = "";
                java.io.File dest = null;
                java.io.File saveFile = null;

                try {
                    // 判断是否能够上传至本地
                    if ("1".equals(uploadLocal)) {
                        // 保存本地，创建目录
                        java.io.File file1 = new java.io.File(newPath);
                        if (!file1.exists()) {
                            file1.mkdirs();
                        }
                        saveFile = new java.io.File(saveUrl);
                    }

                    // 上传七牛云，判断是否能够上传七牛云
                    if ("1".equals(uploadQiNiu)) {
                        // 创建一个临时目录文件
                        String tempFiles = "temp/" + newFileName;
                        dest = new java.io.File(tempFiles);
                        if (!dest.getParentFile().exists()) {
                            dest.getParentFile().mkdirs();
                        }
                        out = new BufferedOutputStream(new FileOutputStream(dest));
                        out.write(file.getBytes());
                        out.flush();
                        out.close();
                        qiNiuUrl = qn.uploadQiniu(dest, qiNiuConfig);
                    }

                    // 判断是否能够上传至本地
                    if ("1".equals(uploadLocal)) {
                        // 序列化文件到本地
                        saveFile.createNewFile();
                        file.transferTo(saveFile);
                    }

                    // 图片数据插入数据库
                    File picFile = new File();
                    picFile.setFileSortUid(fileSort.getUid());
                    picFile.setFileOldName(oldName);
                    picFile.setFileSize(fileSize);
                    picFile.setPicExpandedName(picExpandedName);
                    picFile.setPicName(newFileName);
                    picFile.setPicUrl(picurl);
                    picFile.setStatus(StatusEnum.ENABLE);
                    picFile.setUserUid(userUid);
                    picFile.setAdminUid(adminUid);
                    picFile.setQiNiuUrl(qiNiuUrl);
                    picFile.insert();
                    lists.add(picFile);

                } catch (Exception e) {
                    log.info("==上传文件异常===url:" + saveUrl + "-----");
                    e.printStackTrace();
                    return ResultUtils.result(SystemConstants.ERROR, "文件上传失败，请检查系统配置");
                } finally {
                    if (dest != null && dest.getParentFile().exists()) {
                        dest.delete();
                    }
                }
            }

            return ResultUtils.result(SystemConstants.SUCCESS, lists);
        }

        return ResultUtils.result(SystemConstants.ERROR, "请上传图片");
    }
}
