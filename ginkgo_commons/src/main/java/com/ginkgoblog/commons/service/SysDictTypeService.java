package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.SysDictType;
import com.ginkgoblog.commons.vo.SysDictTypeVO;

import java.util.List;

/**
 * 字典类型表 Service 层
 *
 * @author maomao
 * @date 2021-02-01
 */
public interface SysDictTypeService extends IService<SysDictType> {
    /**
     * 获取字典类型列表
     *
     * @param sysDictTypeVO
     * @return
     */
    IPage<SysDictType> getPageList(SysDictTypeVO sysDictTypeVO);

    /**
     * 新增字典类型
     *
     * @param sysDictTypeVO
     */
    String addSysDictType(SysDictTypeVO sysDictTypeVO);

    /**
     * 编辑字典类型
     *
     * @param sysDictTypeVO
     */
    String editSysDictType(SysDictTypeVO sysDictTypeVO);

    /**
     * 批量删除字典类型
     *
     * @param sysDictTypeVOList
     */
    String deleteBatchSysDictType(List<SysDictTypeVO> sysDictTypeVOList);
}
