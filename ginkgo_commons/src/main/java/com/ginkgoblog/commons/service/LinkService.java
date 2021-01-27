package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.Link;

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
     * 点击友链
     *
     * @return
     */
    String addLinkCount(String uid);
}
