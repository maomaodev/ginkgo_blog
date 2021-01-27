package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.base.constants.SqlConstants;
import com.ginkgoblog.base.enums.EStatus;
import com.ginkgoblog.commons.entity.BlogSort;
import com.ginkgoblog.commons.mapper.BlogSortMapper;
import com.ginkgoblog.commons.service.BlogSortService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 博客分类表 Service 层实现类
 *
 * @author maomao
 * @date 2021-01-24
 */
@Service
public class BlogSortServiceImpl extends ServiceImpl<BlogSortMapper, BlogSort> implements BlogSortService {


    @Override
    public List<BlogSort> getList() {
        QueryWrapper<BlogSort> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SqlConstants.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SqlConstants.SORT);
        return this.list(queryWrapper);
    }
}
