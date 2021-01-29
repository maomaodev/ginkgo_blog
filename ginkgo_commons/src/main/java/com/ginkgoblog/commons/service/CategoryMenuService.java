package com.ginkgoblog.commons.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ginkgoblog.commons.entity.CategoryMenu;
import com.ginkgoblog.commons.vo.CategoryMenuVO;

import java.util.List;
import java.util.Map;

/**
 * 菜单表 Service 层
 *
 * @author maomao
 * @date 2021-01-17
 */
public interface CategoryMenuService extends IService<CategoryMenu> {
    /**
     * 获取菜单列表
     *
     * @param categoryMenuVO
     * @return
     */
    Map<String, Object> getPageList(CategoryMenuVO categoryMenuVO);

    /**
     * 获取全部菜单列表
     *
     * @param keyword
     * @return
     */
    List<CategoryMenu> getAllList(String keyword);

    /**
     * 获取所有二级菜单-按钮列表
     *
     * @param keyword
     * @return
     */
    List<CategoryMenu> getButtonAllList(String keyword);

    /**
     * 新增菜单
     *
     * @param categoryMenuVO
     */
    String addCategoryMenu(CategoryMenuVO categoryMenuVO);

    /**
     * 编辑菜单
     *
     * @param categoryMenuVO
     */
    String editCategoryMenu(CategoryMenuVO categoryMenuVO);

    /**
     * 批量删除菜单
     *
     * @param categoryMenuVO
     */
    String deleteCategoryMenu(CategoryMenuVO categoryMenuVO);

    /**
     * 置顶菜单
     *
     * @param categoryMenuVO
     */
    String stickCategoryMenu(CategoryMenuVO categoryMenuVO);
}
