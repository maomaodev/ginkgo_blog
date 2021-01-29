package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.base.enums.EStatus;
import com.ginkgoblog.commons.constants.MessageConf;
import com.ginkgoblog.commons.constants.SQLConf;
import com.ginkgoblog.commons.constants.SysConf;
import com.ginkgoblog.commons.entity.Blog;
import com.ginkgoblog.commons.entity.Picture;
import com.ginkgoblog.commons.entity.PictureSort;
import com.ginkgoblog.commons.feign.PictureFeignClient;
import com.ginkgoblog.commons.mapper.PictureMapper;
import com.ginkgoblog.commons.service.BlogService;
import com.ginkgoblog.commons.service.PictureService;
import com.ginkgoblog.commons.service.PictureSortService;
import com.ginkgoblog.commons.utils.WebUtil;
import com.ginkgoblog.commons.vo.PictureVO;
import com.ginkgoblog.utils.ResultUtil;
import com.ginkgoblog.utils.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 图片表 Service 接口实现类
 *
 * @author maomao
 * @date 2021-01-29
 */
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {

    @Autowired
    private WebUtil webUtil;
    @Autowired
    private BlogService blogService;
    @Autowired
    private PictureService pictureService;
    @Autowired
    private PictureSortService pictureSortService;
    @Autowired
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public IPage<Picture> getPageList(PictureVO pictureVO) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(pictureVO.getKeyword()) && !StringUtils.isEmpty(pictureVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.PIC_NAME, pictureVO.getKeyword().trim());
        }

        Page<Picture> page = new Page<>();
        page.setCurrent(pictureVO.getCurrentPage());
        page.setSize(pictureVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.PICTURE_SORT_UID, pictureVO.getPictureSortUid());
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        IPage<Picture> pageList = pictureService.page(page, queryWrapper);
        List<Picture> pictureList = pageList.getRecords();

        final StringBuffer fileUids = new StringBuffer();
        pictureList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUids.append(item.getFileUid() + SysConf.FILE_SEGMENTATION);
            }
        });

        String pictureResult = null;
        Map<String, String> pictureMap = new HashMap<>();

        if (fileUids != null) {
            pictureResult = this.pictureFeignClient.getPicture(fileUids.toString(), SysConf.FILE_SEGMENTATION);
        }
        List<Map<String, Object>> picList = webUtil.getPictureMap(pictureResult);

        picList.forEach(item -> {
            pictureMap.put(item.get(SysConf.UID).toString(), item.get(SysConf.URL).toString());
        });

        for (Picture item : pictureList) {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                item.setPictureUrl(pictureMap.get(item.getFileUid()));
            }
        }
        pageList.setRecords(pictureList);
        return pageList;
    }

    @Override
    public String addPicture(List<PictureVO> pictureVOList) {
        List<Picture> pictureList = new ArrayList<>();
        if (pictureVOList.size() > 0) {
            for (PictureVO pictureVO : pictureVOList) {
                Picture picture = new Picture();
                picture.setFileUid(pictureVO.getFileUid());
                picture.setPictureSortUid(pictureVO.getPictureSortUid());
                picture.setPicName(pictureVO.getPicName());
                picture.setStatus(EStatus.ENABLE);
                pictureList.add(picture);
            }
            pictureService.saveBatch(pictureList);
        } else {
            return ResultUtil.result(SysConf.ERROR, MessageConf.INSERT_FAIL);
        }
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editPicture(PictureVO pictureVO) {
        Picture picture = pictureService.getById(pictureVO.getUid());
        // 这里需要更新所有的博客，将图片替换成 裁剪的图片
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.FILE_UID, picture.getFileUid());
        List<Blog> blogList = blogService.list(queryWrapper);
        if (blogList.size() > 0) {
            blogList.forEach(item -> {
                item.setFileUid(pictureVO.getFileUid());
            });
            blogService.updateBatchById(blogList);

            Map<String, Object> map = new HashMap<>();
            map.put(SysConf.COMMAND, SysConf.EDIT_BATCH);

            //发送到RabbitMq
            rabbitTemplate.convertAndSend(SysConf.EXCHANGE_DIRECT, SysConf.MOGU_BLOG, map);
        }
        picture.setFileUid(pictureVO.getFileUid());
        picture.setPicName(pictureVO.getPicName());
        picture.setPictureSortUid(pictureVO.getPictureSortUid());
        picture.setUpdateTime(new Date());
        picture.updateById();
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchPicture(PictureVO pictureVO) {
        // 参数校验
        // 图片删除的时候，是携带多个id拼接而成的
        String uidStr = pictureVO.getUid();
        if (StringUtils.isEmpty(uidStr)) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = StringUtils.changeStringToString(pictureVO.getUid(), SysConf.FILE_SEGMENTATION);
        for (String item : uids) {
            Picture picture = pictureService.getById(item);
            picture.setStatus(EStatus.DISABLED);
            picture.setUpdateTime(new Date());
            picture.updateById();
        }
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.DELETE_SUCCESS);
    }

    @Override
    public String setPictureCover(PictureVO pictureVO) {
        PictureSort pictureSort = pictureSortService.getById(pictureVO.getPictureSortUid());
        if (pictureSort != null) {
            Picture picture = pictureService.getById(pictureVO.getUid());
            if (picture != null) {
                pictureSort.setFileUid(picture.getFileUid());
                picture.setUpdateTime(new Date());
                pictureSort.updateById();
            } else {
                return ResultUtil.result(SysConf.ERROR, MessageConf.The_PICTURE_DOES_NOT_EXIST);
            }
        } else {
            return ResultUtil.result(SysConf.ERROR, MessageConf.The_PICTURE_SORT_DOES_NOT_EXIST);
        }
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public Picture getTopOne() {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByAsc(SQLConf.CREATE_TIME);
        queryWrapper.last(SysConf.LIMIT_ONE);
        Picture picture = pictureService.getOne(queryWrapper);
        return picture;
    }
}
