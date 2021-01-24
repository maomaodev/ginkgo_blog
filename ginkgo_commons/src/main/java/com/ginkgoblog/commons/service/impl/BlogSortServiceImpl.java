package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.commons.entity.BlogSort;
import com.ginkgoblog.commons.mapper.BlogSortMapper;
import com.ginkgoblog.commons.service.BlogSortService;
import org.springframework.stereotype.Service;

/**
 * 博客分类表 Service 层实现类
 *
 * @author maomao
 * @date 2021-01-24
 */
@Service
public class BlogSortServiceImpl extends ServiceImpl<BlogSortMapper, BlogSort> implements BlogSortService {
}
