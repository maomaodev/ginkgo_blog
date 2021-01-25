package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.commons.entity.CategoryMenu;
import com.ginkgoblog.commons.mapper.CategoryMenuMapper;
import com.ginkgoblog.commons.service.CategoryMenuService;
import org.springframework.stereotype.Service;

/**
 * 菜单表 Service 层实现类
 *
 * @author maomao
 * @date 2021-01-25
 */
@Service
public class CategoryMenuServiceImpl extends ServiceImpl<CategoryMenuMapper, CategoryMenu> implements CategoryMenuService {

}
