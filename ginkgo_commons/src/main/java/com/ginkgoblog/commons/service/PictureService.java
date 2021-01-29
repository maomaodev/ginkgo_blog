package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.Picture;
import com.ginkgoblog.commons.vo.PictureVO;

import java.util.List;

/**
 * 图片表 Service 接口
 *
 * @author maomao
 * @date 2021-01-29
 */
public interface PictureService extends IService<Picture> {
    /**
     * 获取图片列表
     *
     * @param pictureVO
     * @return
     */
     IPage<Picture> getPageList(PictureVO pictureVO);

    /**
     * 新增图片
     *
     * @param pictureVOList
     */
     String addPicture(List<PictureVO> pictureVOList);

    /**
     * 编辑图片
     *
     * @param pictureVO
     */
     String editPicture(PictureVO pictureVO);

    /**
     * 批量删除图片
     *
     * @param pictureVO
     */
     String deleteBatchPicture(PictureVO pictureVO);

    /**
     * 设置图片封面
     *
     * @param pictureVO
     */
     String setPictureCover(PictureVO pictureVO);

    /**
     * 获取最新图片,按时间排序
     *
     * @return
     */
     Picture getTopOne();
}
