package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.SysDictData;
import com.ginkgoblog.commons.vo.SysDictDataVO;

import java.util.List;
import java.util.Map;

/**
 * 字典数据表 Service 层
 *
 * @author maomao
 * @date 2021-02-01
 */
public interface SysDictDataService extends IService<SysDictData> {
    /**
     * 获取数据字典列表
     *
     * @param sysDictDataVO
     * @return
     */
    IPage<SysDictData> getPageList(SysDictDataVO sysDictDataVO);

    /**
     * 新增数据字典
     *
     * @param sysDictDataVO
     */
    String addSysDictData(SysDictDataVO sysDictDataVO);

    /**
     * 编辑数据字典
     *
     * @param sysDictDataVO
     */
    String editSysDictData(SysDictDataVO sysDictDataVO);

    /**
     * 批量删除数据字典
     *
     * @param sysDictDataVOList
     */
    public String deleteBatchSysDictData(List<SysDictDataVO> sysDictDataVOList);

    /**
     * 根据字典类型获取字典数据
     *
     * @param dictType
     * @return
     */
    Map<String, Object> getListByDictType(String dictType);

    /**
     * 根据字典类型数组获取字典数据
     *
     * @param dictTypeList
     * @return
     */
    Map<String, Map<String, Object>> getListByDictTypeList(List<String> dictTypeList);

}
