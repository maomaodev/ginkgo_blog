package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.commons.entity.Comment;
import com.ginkgoblog.commons.mapper.CommentMapper;
import com.ginkgoblog.commons.service.CommentService;
import org.springframework.stereotype.Service;

/**
 * 评论表 Service 层实现类
 *
 * @author maomao
 * @date 2021-01-25
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
