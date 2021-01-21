package com.ginkgoblog.picture.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.picture.entity.FileSort;
import com.ginkgoblog.picture.mapper.FileSortMapper;
import com.ginkgoblog.picture.service.FileSortService;
import org.springframework.stereotype.Service;

/**
 * 文件分类表 Service 层实现类
 *
 * @author maomao
 * @date 2021-01-21
 */
@Service
public class FileSortServiceImpl extends ServiceImpl<FileSortMapper, FileSort> implements FileSortService {
}
