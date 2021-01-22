package com.ginkgoblog.picture.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ginkgoblog.base.constants.SqlConstants;
import com.ginkgoblog.base.constants.SystemConstants;
import com.ginkgoblog.picture.entity.File;
import com.ginkgoblog.picture.service.FileService;
import com.ginkgoblog.picture.service.FileSortService;
import com.ginkgoblog.picture.utils.FeignUtil;
import com.ginkgoblog.utils.ResultUtils;
import com.ginkgoblog.utils.StringUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件表 Controller 层
 *
 * @author maomao
 * @date 2021-01-21
 */
@Slf4j
@RestController
@RequestMapping("/file")
@Api("图片服务相关接口")
public class FileController {
    @Autowired
    FeignUtil feignUtil;
    @Autowired
    private FileService fileService;
    @Autowired
    private FileSortService fileSortService;
    @Value(value = "${file.upload.path}")
    private String path;

    /**
     * 获取文件的信息接口
     *
     * @param fileIds 获取文件信息的ids
     * @param code    ids用什么分割的，默认“,”
     * @return 处理结果
     */
    @ApiOperation("通过fileIds获取图片信息接口")
    @GetMapping("/getPicture")
    public String getPicture(
            @ApiParam(name = "fileIds", value = "文件ids") @RequestParam(name = "fileIds", required = false) String fileIds,
            @ApiParam(name = "code", value = "切割符") @RequestParam(name = "code", required = false) String code) {

        if (StringUtils.isEmpty(code)) {
            code = ",";
        }
        if (StringUtils.isEmpty(fileIds)) {
            return ResultUtils.result(SystemConstants.ERROR, "数据错误");
        } else {
            List<Map<String, Object>> list = new ArrayList<>();
            List<String> uids = StringUtils.changeStringToString(fileIds, code);
            QueryWrapper<File> queryWrapper = new QueryWrapper<>();
            queryWrapper.in(SqlConstants.UID, uids);
            List<File> fileList = fileService.list(queryWrapper);

            if (fileList.size() > 0) {
                for (File file : fileList) {
                    Map<String, Object> map = new HashMap<>();

                    // 获取七牛云地址
                    map.put(SqlConstants.QI_NIU_URL, file.getQiNiuUrl());
                    // 获取本地地址
                    map.put(SqlConstants.URL, file.getPicUrl());
                    // 后缀名，也就是类型
                    map.put(SqlConstants.EXPANDED_NAME, file.getPicExpandedName());
                    // 原文件名
                    map.put(SqlConstants.FILE_OLD_NAME, file.getFileOldName());
                    map.put("file_old_name", file.getFileOldName());
                    // 现文件名和id
                    map.put(SqlConstants.NAME, file.getPicName());
                    map.put(SqlConstants.UID, file.getUid());

                    list.add(map);
                }
            }

            return ResultUtils.result(SystemConstants.SUCCESS, list);
        }
    }

    /**
     * 文件下载
     *
     * @param request
     * @param response
     * @return
     */
    @ApiOperation("图片下载接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileIds", value = "fileIds", dataType = "String")
    })
    @GetMapping("downloadFile")
    public String downloadFile(HttpServletRequest request, HttpServletResponse response) {

        String fileId = request.getParameter("fileId");
        if (StringUtils.isEmpty(fileId)) {
            File oneById = fileService.getById(fileId);
            if (oneById != null) {
                String fileName = request.getParameter("fileName");
                // 为空则使用之前的文件名
                if (StringUtils.isEmpty(fileName)) {
                    fileName = oneById.getFileOldName();
                }

                String fileRealPath = path + oneById.getPicUrl();
                java.io.File file = new java.io.File(fileRealPath);
                // 设置强制下载不打开
                response.setContentType("application/force-download");
                // 设置文件名
                response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);

                // 文件下载，还可以直接使用common-io库的FileUtils工具类直接下载
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    log.info("文件下载成功 realUrl:   " + fileRealPath);
                    return ResultUtils.result(SystemConstants.SUCCESS, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("============文件下载出现异常==========");
                    return ResultUtils.result(SystemConstants.ERROR, "文件下载出现异常");
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        log.info("文件下载失败fileId=   " + fileId);
        return ResultUtils.result(SystemConstants.ERROR, "参数错误");
    }

    @ApiOperation("多图片上传接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filedatas", value = "文件数据", required = true),
            @ApiImplicitParam(name = "userUid", value = "用户UID", dataType = "String"),
            @ApiImplicitParam(name = "sysUserId", value = "管理员UID", dataType = "String"),
            @ApiImplicitParam(name = "projectName", value = "项目名", dataType = "String"),
            @ApiImplicitParam(name = "sortName", value = "模块名", dataType = "String")
    })
    @PostMapping("/pictures")
    public synchronized String uploadPics(HttpServletRequest request, List<MultipartFile> filedatas) {

        String token = request.getParameter(SqlConstants.TOKEN);
        // 获取七牛云配置
        Map<String, String> qiNiuResultMap = feignUtil.getQiNiuConfig(token);
        // 七牛云配置
        Map<String, String> qiNiuConfig = new HashMap<>();

        String uploadQiNiu = qiNiuResultMap.get("uploadQiNiu");
        String uploadLocal = qiNiuResultMap.get("uploadLocal");
        String localPictureBaseUrl = qiNiuResultMap.get("localPictureBaseUrl");
        String qiNiuPictureBaseUrl = qiNiuResultMap.get("qiNiuPictureBaseUrl");

        String qiNiuAccessKey = qiNiuResultMap.get("qiNiuAccessKey");
        String qiNiuSecretKey = qiNiuResultMap.get("qiNiuSecretKey");
        String qiNiuBucket = qiNiuResultMap.get("qiNiuBucket");
        String qiNiuArea = qiNiuResultMap.get("qiNiuArea");

        if ("1".equals(uploadQiNiu) && (StringUtils.isEmpty(qiNiuPictureBaseUrl) || StringUtils.isEmpty(qiNiuAccessKey)
                || StringUtils.isEmpty(qiNiuSecretKey) || StringUtils.isEmpty(qiNiuBucket) || StringUtils.isEmpty(qiNiuArea))) {
            return ResultUtils.result(SystemConstants.ERROR, "请先配置七牛云");
        }

        if ("1".equals(uploadLocal) && StringUtils.isEmpty(localPictureBaseUrl)) {
            return ResultUtils.result(SystemConstants.ERROR, "请先配置本地图片域名");
        }

        qiNiuConfig.put("qiNiuAccessKey", qiNiuAccessKey);
        qiNiuConfig.put("qiNiuSecretKey", qiNiuSecretKey);
        qiNiuConfig.put("qiNiuBucket", qiNiuBucket);
        qiNiuConfig.put("qiNiuArea", qiNiuArea);
        qiNiuConfig.put("uploadQiNiu", uploadQiNiu);
        qiNiuConfig.put("uploadLocal", uploadLocal);

        return fileService.uploadImages(path, request, filedatas, qiNiuConfig);
    }

}
