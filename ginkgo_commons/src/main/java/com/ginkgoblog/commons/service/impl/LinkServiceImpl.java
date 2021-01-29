package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.base.constants.BaseSQLConf;
import com.ginkgoblog.base.enums.ELinkStatus;
import com.ginkgoblog.base.enums.EStatus;
import com.ginkgoblog.commons.constants.MessageConf;
import com.ginkgoblog.commons.constants.SQLConf;
import com.ginkgoblog.commons.constants.SysConf;
import com.ginkgoblog.commons.entity.Link;
import com.ginkgoblog.commons.mapper.LinkMapper;
import com.ginkgoblog.commons.service.LinkService;
import com.ginkgoblog.commons.vo.LinkVO;
import com.ginkgoblog.utils.ResultUtil;
import com.ginkgoblog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 友链表 Service 接口实现类
 *
 * @author maomao
 * @date 2021-01-27
 */
@Service
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {
    @Resource
    LinkMapper linkMapper;

    @Autowired
    LinkService linkService;

    @Override
    public List<Link> getListByPageSize(Integer pageSize) {
        QueryWrapper<Link> queryWrapper = new QueryWrapper<>();
        Page<Link> page = new Page<>();
        page.setCurrent(1);
        page.setSize(pageSize);
        queryWrapper.eq(BaseSQLConf.LINK_STATUS, ELinkStatus.PUBLISH);
        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(BaseSQLConf.SORT);
        IPage<Link> pageList = linkMapper.selectPage(page, queryWrapper);
        return pageList.getRecords();
    }

    @Override
    public IPage<Link> getPageList(LinkVO linkVO) {
        QueryWrapper<Link> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(linkVO.getKeyword()) && !StringUtils.isEmpty(linkVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.TITLE, linkVO.getKeyword().trim());
        }

        if (linkVO.getLinkStatus() != null) {
            queryWrapper.eq(SQLConf.LINK_STATUS, linkVO.getLinkStatus());
        }

        Page<Link> page = new Page<>();
        page.setCurrent(linkVO.getCurrentPage());
        page.setSize(linkVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.SORT);
        IPage<Link> pageList = linkService.page(page, queryWrapper);
        return pageList;
    }

    @Override
    public String addLink(LinkVO linkVO) {
        Link link = new Link();
        link.setTitle(linkVO.getTitle());
        link.setSummary(linkVO.getSummary());
        link.setUrl(linkVO.getUrl());
        link.setClickCount(0);
        link.setLinkStatus(linkVO.getLinkStatus());
        link.setSort(linkVO.getSort());
        link.setStatus(EStatus.ENABLE);
        link.setUpdateTime(new Date());
        link.insert();
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editLink(LinkVO linkVO) {
        Link link = linkService.getById(linkVO.getUid());
        link.setTitle(linkVO.getTitle());
        link.setSummary(linkVO.getSummary());
        link.setLinkStatus(linkVO.getLinkStatus());
        link.setUrl(linkVO.getUrl());
        link.setSort(linkVO.getSort());
        link.setUpdateTime(new Date());
        link.updateById();
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteLink(LinkVO linkVO) {
        Link link = linkService.getById(linkVO.getUid());
        link.setStatus(EStatus.DISABLED);
        link.setUpdateTime(new Date());
        link.updateById();
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.DELETE_SUCCESS);
    }

    @Override
    public String stickLink(LinkVO linkVO) {
        Link link = linkService.getById(linkVO.getUid());

        //查找出最大的那一个
        QueryWrapper<Link> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(SQLConf.SORT);
        Page<Link> page = new Page<>();
        page.setCurrent(0);
        page.setSize(1);
        IPage<Link> pageList = linkService.page(page, queryWrapper);
        List<Link> list = pageList.getRecords();
        Link maxSort = list.get(0);
        if (StringUtils.isEmpty(maxSort.getUid())) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.PARAM_INCORRECT);
        }
        if (maxSort.getUid().equals(link.getUid())) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.OPERATION_FAIL);
        }
        Integer sortCount = maxSort.getSort() + 1;
        link.setSort(sortCount);
        link.setUpdateTime(new Date());
        link.updateById();
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public String addLinkCount(String uid) {
        if (StringUtils.isEmpty(uid)) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.PARAM_INCORRECT);
        }
        Link link = linkService.getById(uid);
        if (link != null) {
            int count = link.getClickCount() + 1;
            link.setClickCount(count);
            link.updateById();
        } else {
            return ResultUtil.result(SysConf.ERROR, MessageConf.PARAM_INCORRECT);
        }

        return ResultUtil.result(SysConf.SUCCESS, MessageConf.UPDATE_SUCCESS);
    }
}
