package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.Tag;
import com.ginkgoblog.commons.vo.TagVO;

import java.util.List;

/**
 * 标签表 Service 层
 *
 * @author maomao
 * @date 2021-01-17
 */
public interface TagService extends IService<Tag> {

    /**
     * 获取博客标签列表
     *
     * @param tagVO
     * @return
     */
    IPage<Tag> getPageList(TagVO tagVO);

    /**
     * 获取全部博客标签列表
     *
     * @return
     */
    List<Tag> getList();

    /**
     * 新增博客标签
     *
     * @param tagVO
     */
    String addTag(TagVO tagVO);

    /**
     * 编辑博客标签
     *
     * @param tagVO
     */
    String editTag(TagVO tagVO);

    /**
     * 批量删除博客标签
     *
     * @param tagVOList
     */
    String deleteBatchTag(List<TagVO> tagVOList);

    /**
     * 置顶博客标签
     *
     * @param tagVO
     */
    String stickTag(TagVO tagVO);

    /**
     * 通过点击量排序博客
     *
     * @return
     */
    String tagSortByClickCount();

    /**
     * 通过引用量排序博客
     *
     * @return
     */
    String tagSortByCite();

    /**
     * 获取热门标签
     *
     * @return
     */
    IPage<Tag> getHotTag(Integer hotTagCount);

    /**
     * 获取一个排序最高的标签
     *
     * @return
     */
    Tag getTopTag();
}
