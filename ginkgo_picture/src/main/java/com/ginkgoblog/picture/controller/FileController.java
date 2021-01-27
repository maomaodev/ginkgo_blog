package com.ginkgoblog.picture.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ginkgoblog.base.constants.SqlConstants;
import com.ginkgoblog.base.constants.SystemConstants;
import com.ginkgoblog.base.enums.EOpenStatus;
import com.ginkgoblog.base.enums.EStatus;
import com.ginkgoblog.commons.vo.FileVO;
import com.ginkgoblog.picture.entity.File;
import com.ginkgoblog.picture.entity.FileSort;
import com.ginkgoblog.picture.service.FileService;
import com.ginkgoblog.picture.service.FileSortService;
import com.ginkgoblog.picture.utils.FeignUtil;
import com.ginkgoblog.picture.utils.QiniuUtil;
import com.ginkgoblog.utils.DateUtils;
import com.ginkgoblog.utils.ResultUtils;
import com.ginkgoblog.utils.StringUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

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

    @ApiOperation("截图上传")
    @PostMapping("/cropperPicture")
    public String cropperPicture(@RequestParam("file") MultipartFile file,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws IOException {

        List<MultipartFile> filedatas = new ArrayList<>();
        filedatas.add(file);

        String platform = request.getParameter(SqlConstants.PLATFORM);
        String token = request.getParameter(SqlConstants.TOKEN);

        // 获取七牛云配置文件
        Map<String, String> qiNiuResultMap = new HashMap<>();
        // 判断是否是web端发送过来的请求
        if (SqlConstants.WEB.equals(platform)) {
            // 如果是调用web端获取配置的接口
            qiNiuResultMap = feignUtil.getQiNiuConfigByWebToken(token);
        } else {
            // 调用admin端获取配置接口
            qiNiuResultMap = feignUtil.getQiNiuConfig(token);
        }

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
        String picturePriority = qiNiuResultMap.get("picturePriority");

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

        // 多文件上传后，返回文件的数据库相关信息
        String result = fileService.uploadImages(path, request, filedatas, qiNiuConfig);
        // 返回的数据，包含每张图片的UID和URL
        List<Map<String, Object>> listMap = new ArrayList<>();
        Map<String, Object> picMap = (Map<String, Object>) JSON.parse(result);
        if (SystemConstants.SUCCESS.equals(picMap.get(SystemConstants.CODE))) {
            List<Map<String, Object>> picData = (List<Map<String, Object>>) picMap.get("data");
            if (picData.size() > 0) {
                for (Map<String, Object> picDatum : picData) {
                    Map<String, Object> item = new HashMap<>();
                    // 设置图片的UID
                    item.put(SqlConstants.UID, picDatum.get(SqlConstants.UID));
                    // 设置图片的URL（七牛云或本地）
                    if ("1".equals(picturePriority)) {
                        item.put(SqlConstants.URL, qiNiuPictureBaseUrl + picDatum.get(SqlConstants.QI_NIU_URL));
                    } else {
                        item.put(SqlConstants.URL, localPictureBaseUrl + picDatum.get(SqlConstants.PIC_URL));
                    }
                    listMap.add(item);
                }
            }
        }

        return ResultUtils.result(SystemConstants.SUCCESS, listMap);
    }

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
        String picturePriority = qiNiuResultMap.get("picturePriority");

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

    @ApiOperation(value = "通过URL上传图片", notes = "通过URL上传图片")
    @PostMapping("/uploadPicsByUrl")
    public synchronized Object uploadPicsByUrl(HttpServletRequest request, @RequestBody FileVO fileVO) {

        String token = request.getParameter(SqlConstants.TOKEN);
        // 获取七牛云配置文件
        Map<String, String> qiNiuResultMap = feignUtil.getQiNiuConfig(token);
        // 七牛云配置
        Map<String, String> qiNiuConfig = new HashMap<>();

        String uploadQiNiu = "";
        String uploadLocal = "";
        String localPictureBaseUrl = "";
        String qiNiuPictureBaseUrl = "";

        String qiNiuAccessKey = "";
        String qiNiuSecretKey = "";
        String qiNiuBucket = "";
        String qiNiuArea = "";
        String picturePriority = "";

        uploadQiNiu = qiNiuResultMap.get("uploadQiNiu");
        uploadLocal = qiNiuResultMap.get("uploadLocal");
        localPictureBaseUrl = qiNiuResultMap.get("localPictureBaseUrl");
        qiNiuPictureBaseUrl = qiNiuResultMap.get("qiNiuPictureBaseUrl");

        qiNiuAccessKey = qiNiuResultMap.get("qiNiuAccessKey");
        qiNiuSecretKey = qiNiuResultMap.get("qiNiuSecretKey");
        qiNiuBucket = qiNiuResultMap.get("qiNiuBucket");
        qiNiuArea = qiNiuResultMap.get("qiNiuArea");
        picturePriority = qiNiuResultMap.get("picturePriority");

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

        String userUid = fileVO.getUserUid();
        String adminUid = fileVO.getAdminUid();
        String projectName = fileVO.getProjectName();
        String sortName = fileVO.getSortName();
        List<String> urlList = fileVO.getUrlList();

        // projectName现在默认base
        if (StringUtils.isEmpty(projectName)) {
            projectName = "base";
        }

        // 这里可以检测用户上传，如果不是网站的用户或会员就不能调用
        if (StringUtils.isEmpty(userUid) && StringUtils.isEmpty(adminUid)) {
            return ResultUtils.result(SystemConstants.ERROR, "请先注册");
        }

        log.info("####### fileSorts" + projectName + " ###### " + sortName);

        QueryWrapper<FileSort> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SqlConstants.SORT_NAME, sortName);
        queryWrapper.eq(SqlConstants.PROJECT_NAME, projectName);
        queryWrapper.eq(SqlConstants.STATUS, EStatus.ENABLE);
        List<FileSort> fileSorts = fileSortService.list(queryWrapper);

        FileSort fileSort;
        if (fileSorts.size() > 0) {
            fileSort = fileSorts.get(0);
            log.info("====fileSort====" + JSON.toJSONString(fileSort));
        } else {
            return ResultUtils.result(SystemConstants.ERROR, "文件不被允许上传");
        }

        String sortUrl = fileSort.getUrl();

        //判断url是否为空，如果为空，使用默认
        if (StringUtils.isEmpty(sortUrl)) {
            sortUrl = "base/common/";
        } else {
            sortUrl = fileSort.getUrl();
        }

        List<File> lists = new ArrayList<>();

        // 文件上传
        if (urlList != null && urlList.size() > 0) {
            for (String itemUrl : urlList) {

                //获取新文件名(默认为jpg)
                String newFileName = System.currentTimeMillis() + ".jpg";
                //文件绝对路径
                String newPath = path + sortUrl + "/jpg/" + DateUtils.getYears() + "/"
                        + DateUtils.getMonth() + "/" + DateUtils.getDay() + "/";
                //文件相对路径
                String picurl = sortUrl + "/jpg/" + DateUtils.getYears() + "/"
                        + DateUtils.getMonth() + "/" + DateUtils.getDay() + "/" + newFileName;
                String saveUrl = newPath + newFileName;
                log.info("newPath:" + newPath);
                log.info("saveUrl:" + saveUrl);

                // 将图片上传到本地服务器中以及七牛云中
                BufferedOutputStream out;
                String qiNiuUrl = "";
                java.io.File dest = null;
                FileOutputStream os = null;
                InputStream inputStream = null;

                // 判断是否能够上传至本地
                if ("1".equals(uploadLocal)) {
                    // 判断文件是否存在
                    java.io.File file1 = new java.io.File(newPath);
                    if (!file1.exists()) {
                        file1.mkdirs();
                    }
                    try {
                        // 构造URL
                        URL url = new URL(itemUrl);

                        // 打开连接
                        URLConnection con = url.openConnection();

                        // 设置10秒
                        con.setConnectTimeout(10000);
                        con.setReadTimeout(10000);

                        // 当获取的相片无法正常显示的时候，需要给一个默认图片
                        inputStream = con.getInputStream();

                        // 1K的数据缓冲
                        byte[] bs = new byte[1024];
                        // 读取到的数据长度
                        int len;

                        java.io.File file = new java.io.File(saveUrl);
                        os = new FileOutputStream(file, true);
                        // 开始读取
                        while ((len = inputStream.read(bs)) != -1) {
                            os.write(bs, 0, len);
                        }


                    } catch (Exception e) {
                        log.info("==上传文件异常===url:" + saveUrl + "-----");
                        e.printStackTrace();
                        return ResultUtils.result(SystemConstants.ERROR, "获取图片超时，文件上传失败");
                    } finally {
                        try {
                            // 完毕，关闭所有链接
                            os.close();
                            inputStream.close();
                        } catch (Exception e) {
                            log.error(e.getMessage());
                        }
                    }
                }

                // 上传七牛云，判断是否能够上传七牛云
                if ("1".equals(uploadQiNiu)) {
                    try {
                        java.io.File pdfFile = new java.io.File(saveUrl);
                        FileInputStream fileInputStream = new FileInputStream(pdfFile);
                        MultipartFile fileData = new MockMultipartFile(pdfFile.getName(), pdfFile.getName(),
                                ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);

                        // 上传七牛云
                        // 创建一个临时目录文件
                        String tempFiles = "temp/" + newFileName;
                        dest = new java.io.File(tempFiles);
                        if (!dest.getParentFile().exists()) {
                            dest.getParentFile().mkdirs();
                        }
                        out = new BufferedOutputStream(new FileOutputStream(dest));
                        out.write(fileData.getBytes());
                        QiniuUtil qn = new QiniuUtil();

                        // TODO 不关闭流，小图片就无法显示？
                        out.flush();
                        out.close();

                        qiNiuUrl = qn.uploadQiniu(dest, qiNiuConfig);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        return ResultUtils.result(SystemConstants.ERROR, "请先配置七牛云");
                    } finally {
                        if (dest != null && dest.getParentFile().exists()) {
                            dest.delete();
                        }
                    }
                }


                File file = new File();

                file.setCreateTime(new Date(System.currentTimeMillis()));
                file.setFileSortUid(fileSort.getUid());
                file.setFileOldName(itemUrl);
                file.setFileSize(0L);
                file.setPicExpandedName("jpg");
                file.setPicName(newFileName);

                // 设置本地图片
                file.setPicUrl(localPictureBaseUrl + picurl);
                // 设置七牛云图片
                file.setQiNiuUrl(qiNiuPictureBaseUrl + qiNiuUrl);

                file.setStatus(EStatus.ENABLE);
                file.setUserUid(userUid);
                file.setAdminUid(adminUid);
                fileService.save(file);
                lists.add(file);
            }
            // 保存成功返回数据
            return ResultUtils.result(SystemConstants.SUCCESS, lists);
        }
        return ResultUtils.result(SystemConstants.ERROR, "请上传图片");
    }

    @ApiOperation("通过URL上传图片")
    @PostMapping("/uploadPicsByUrl2")
    public Object uploadPicsByUrl2(@RequestBody FileVO fileVO) {
        Map<String, Object> resultMap = fileVO.getSystemConfig();
        String uploadQiNiu = resultMap.get(SqlConstants.UPLOAD_QI_NIU).toString();
        String uploadLocal = resultMap.get(SqlConstants.UPLOAD_LOCAL).toString();
        String localPictureBaseUrl = resultMap.get(SqlConstants.LOCAL_PICTURE_BASE_URL).toString();
        String qiNiuPictureBaseUrl = resultMap.get(SqlConstants.QI_NIU_PICTURE_BASE_URL).toString();
        String qiNiuAccessKey = resultMap.get(SqlConstants.QI_NIU_ACCESS_KEY).toString();
        String qiNiuSecretKey = resultMap.get(SqlConstants.QI_NIU_SECRET_KEY).toString();
        String qiNiuBucket = resultMap.get(SqlConstants.QI_NIU_BUCKET).toString();
        String qiNiuArea = resultMap.get(SqlConstants.QI_NIU_AREA).toString();

        if (EOpenStatus.OPEN.equals(uploadQiNiu) && (StringUtils.isEmpty(qiNiuPictureBaseUrl)
                || StringUtils.isEmpty(qiNiuAccessKey) || StringUtils.isEmpty(qiNiuSecretKey)
                || StringUtils.isEmpty(qiNiuBucket) || StringUtils.isEmpty(qiNiuArea))) {
            return ResultUtils.result(SystemConstants.ERROR, "请先配置七牛云");
        }

        if (EOpenStatus.OPEN.equals(uploadLocal) && StringUtils.isEmpty(localPictureBaseUrl)) {
            return ResultUtils.result(SystemConstants.ERROR, "请先配置本地图片域名");
        }

        // 七牛云配置
        Map<String, String> qiNiuConfig = new HashMap<>();
        qiNiuConfig.put(SqlConstants.QI_NIU_ACCESS_KEY, qiNiuAccessKey);
        qiNiuConfig.put(SqlConstants.QI_NIU_SECRET_KEY, qiNiuSecretKey);
        qiNiuConfig.put(SqlConstants.QI_NIU_BUCKET, qiNiuBucket);
        qiNiuConfig.put(SqlConstants.QI_NIU_AREA, qiNiuArea);
        qiNiuConfig.put(SqlConstants.UPLOAD_QI_NIU, uploadQiNiu);
        qiNiuConfig.put(SqlConstants.UPLOAD_LOCAL, uploadLocal);

        String userUid = fileVO.getUserUid();
        String adminUid = fileVO.getAdminUid();
        String projectName = fileVO.getProjectName();
        String sortName = fileVO.getSortName();
        List<String> urlList = fileVO.getUrlList();

        // projectName默认base
        if (StringUtils.isEmpty(projectName)) {
            projectName = "base";
        }
        // 这里检测用户上传，如果不是网站的用户或会员就不能调用
        if (StringUtils.isEmpty(userUid) && StringUtils.isEmpty(adminUid)) {
            return ResultUtils.result(SystemConstants.ERROR, "请先注册");
        }
        log.info("####### fileSorts" + projectName + " ###### " + sortName);

        QueryWrapper<FileSort> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SqlConstants.SORT_NAME, sortName);
        queryWrapper.eq(SqlConstants.PROJECT_NAME, projectName);
        queryWrapper.eq(SqlConstants.STATUS, EStatus.ENABLE);
        List<FileSort> fileSorts = fileSortService.list(queryWrapper);

        FileSort fileSort;
        if (fileSorts.size() > 0) {
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
        // 文件上传
        if (urlList != null && urlList.size() > 0) {
            for (String itemUrl : urlList) {
                // 获取新文件名(默认为jpg)
                String newFileName = System.currentTimeMillis() + ".jpg";
                // 文件绝对路径
                String newPath = path + sortUrl + "/jpg/" + DateUtils.getYears() + "/"
                        + DateUtils.getMonth() + "/" + DateUtils.getDay() + "/";
                // 文件相对路径
                String picurl = sortUrl + "/jpg/" + DateUtils.getYears() + "/"
                        + DateUtils.getMonth() + "/" + DateUtils.getDay() + "/" + newFileName;
                String saveUrl = newPath + newFileName;
                log.info("newPath:" + newPath);
                log.info("saveUrl:" + saveUrl);

                // 将图片上传到本地服务器中以及七牛云中
                BufferedOutputStream out;
                String qiNiuUrl = "";
                java.io.File dest = null;
                FileOutputStream os = null;
                InputStream inputStream = null;

                // 判断是否能够上传至本地
                if ("1".equals(uploadLocal)) {
                    // 判断文件是否存在
                    java.io.File file1 = new java.io.File(newPath);
                    if (!file1.exists()) {
                        file1.mkdirs();
                    }
                    try {
                        // 构造URL
                        URL url = new URL(itemUrl);
                        // 打开连接
                        URLConnection con = url.openConnection();
                        // 设置用户代理
                        con.setRequestProperty("User-agent", "	Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
                        // 设置10秒
                        con.setConnectTimeout(10000);
                        con.setReadTimeout(10000);
                        // 当获取的相片无法正常显示的时候，需要给一个默认图片
                        inputStream = con.getInputStream();

                        // 1K的数据缓冲
                        byte[] bs = new byte[1024];
                        // 读取到的数据长度
                        int len;
                        java.io.File file = new java.io.File(saveUrl);
                        os = new FileOutputStream(file, true);
                        // 开始读取
                        while ((len = inputStream.read(bs)) != -1) {
                            os.write(bs, 0, len);
                        }

                    } catch (Exception e) {
                        log.info("==上传文件异常===url:" + saveUrl + "-----");
                        e.printStackTrace();
                        return ResultUtils.result(SystemConstants.ERROR, "获取图片超时，文件上传失败");
                    } finally {
                        try {
                            if (os != null) {
                                os.close();
                            }
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage());
                        }
                    }
                }

                // 上传七牛云，判断是否能够上传七牛云
                if ("1".equals(uploadQiNiu)) {
                    try {
                        java.io.File pdfFile = new java.io.File(saveUrl);
                        FileInputStream fileInputStream = new FileInputStream(pdfFile);
                        MultipartFile fileData = new MockMultipartFile(pdfFile.getName(), pdfFile.getName(),
                                ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);

                        // 上传七牛云
                        // 创建一个临时目录文件
                        String tempFiles = "temp/" + newFileName;
                        dest = new java.io.File(tempFiles);
                        if (!dest.getParentFile().exists()) {
                            dest.getParentFile().mkdirs();
                        }
                        out = new BufferedOutputStream(new FileOutputStream(dest));
                        out.write(fileData.getBytes());
                        QiniuUtil qn = new QiniuUtil();

                        // TODO 不关闭流，小图片就无法显示？
                        out.flush();
                        out.close();
                        qiNiuUrl = qn.uploadQiniu(dest, qiNiuConfig);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        return ResultUtils.result(SystemConstants.ERROR, "请先配置七牛云");
                    } finally {
                        if (dest != null && dest.getParentFile().exists()) {
                            dest.delete();
                        }
                    }
                }


                File file = new File();
                file.setFileSortUid(fileSort.getUid());
                file.setFileOldName(itemUrl);
                file.setFileSize(0L);
                file.setPicExpandedName("jpg");
                file.setPicName(newFileName);

                String url = "";

                // 设置本地图片
                file.setPicUrl(picurl);
                // 设置七牛云图片
                file.setQiNiuUrl(qiNiuUrl);
                file.setStatus(EStatus.ENABLE);
                file.setUserUid(userUid);
                file.setAdminUid(adminUid);
                fileService.save(file);
                lists.add(file);
            }
            //保存成功返回数据
            return ResultUtils.result(SystemConstants.SUCCESS, lists);
        }
        return ResultUtils.result(SystemConstants.ERROR, "请上传图片");


    }
}
