package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.base.constants.SqlConstants;
import com.ginkgoblog.base.enums.StatusEnum;
import com.ginkgoblog.commons.entity.Tag;
import com.ginkgoblog.commons.mapper.TagMapper;
import com.ginkgoblog.commons.service.TagService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 标签表 Service 层实现类
 *
 * @author maomao
 * @date 2021-01-23
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Override
    public List<Tag> getList() {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SqlConstants.STATUS, StatusEnum.ENABLE);
        queryWrapper.orderByDesc(SqlConstants.SORT);
        return this.list(queryWrapper);
    }
}
