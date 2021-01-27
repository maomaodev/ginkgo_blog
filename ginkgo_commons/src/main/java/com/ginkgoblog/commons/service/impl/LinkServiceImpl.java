package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.base.constants.MessageConstants;
import com.ginkgoblog.base.constants.SqlConstants;
import com.ginkgoblog.base.constants.SystemConstants;
import com.ginkgoblog.base.enums.ELinkStatus;
import com.ginkgoblog.base.enums.EStatus;
import com.ginkgoblog.commons.entity.Link;
import com.ginkgoblog.commons.mapper.LinkMapper;
import com.ginkgoblog.commons.service.LinkService;
import com.ginkgoblog.utils.ResultUtils;
import com.ginkgoblog.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 友链表 Service 接口实现类
 *
 * @author maomao
 * @date 2021-01-27
 */
@Service
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {
    @Override
    public List<Link> getListByPageSize(Integer pageSize) {
        QueryWrapper<Link> queryWrapper = new QueryWrapper<>();
        Page<Link> page = new Page<>();
        page.setCurrent(1);
        page.setSize(pageSize);
        queryWrapper.eq(SqlConstants.LINK_STATUS, ELinkStatus.PUBLISH);
        queryWrapper.eq(SqlConstants.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SqlConstants.SORT);
        IPage<Link> pageList = this.page(page, queryWrapper);
        return pageList.getRecords();
    }

    @Override
    public String addLinkCount(String uid) {
        if (StringUtils.isEmpty(uid)) {
            return ResultUtils.result(SystemConstants.ERROR, MessageConstants.PARAM_INCORRECT);
        }

        Link link = this.getById(uid);
        if (link != null) {
            int count = link.getClickCount() + 1;
            link.setClickCount(count);
            link.updateById();
        } else {
            return ResultUtils.result(SystemConstants.ERROR, MessageConstants.PARAM_INCORRECT);
        }

        return ResultUtils.result(SystemConstants.SUCCESS, MessageConstants.UPDATE_SUCCESS);
    }
}
