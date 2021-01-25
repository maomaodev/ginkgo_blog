package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.commons.entity.Collect;
import com.ginkgoblog.commons.mapper.CollectMapper;
import com.ginkgoblog.commons.service.CollectService;
import org.springframework.stereotype.Service;

/**
 * 收藏表 Service 层实现类
 *
 * @author maomao
 * @date 2021-01-25
 */
@Service
public class CollectServiceImpl extends ServiceImpl<CollectMapper, Collect> implements CollectService {
}
