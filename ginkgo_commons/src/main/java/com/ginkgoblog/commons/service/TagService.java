package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.Tag;

import java.util.List;

/**
 * 标签表 Service 层
 *
 * @author maomao
 * @date 2021-01-17
 */
public interface TagService extends IService<Tag> {

    /**
     * 获取全部博客标签列表
     *
     * @return
     */
    List<Tag> getList();
}
