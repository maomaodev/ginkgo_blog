package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.Link;
import com.ginkgoblog.commons.vo.LinkVO;

import java.util.List;

/**
 * 友链表 Service 接口
 *
 * @author maomao
 * @date 2021-01-27
 */
public interface LinkService extends IService<Link> {
    /**
     * 通过页大小获取友链列表
     *
     * @param pageSize
     * @return
     */
    List<Link> getListByPageSize(Integer pageSize);

    /**
     * 获取友链列表
     *
     * @param linkVO
     * @return
     */
    IPage<Link> getPageList(LinkVO linkVO);

    /**
     * 新增友链
     *
     * @param linkVO
     */
    String addLink(LinkVO linkVO);

    /**
     * 编辑友链
     *
     * @param linkVO
     */
    String editLink(LinkVO linkVO);

    /**
     * 删除友链
     *
     * @param linkVO
     */
    String deleteLink(LinkVO linkVO);

    /**
     * 置顶友链
     *
     * @param linkVO
     */
    String stickLink(LinkVO linkVO);

    /**
     * 点击友链
     *
     * @return
     */
    String addLinkCount(String uid);
}
