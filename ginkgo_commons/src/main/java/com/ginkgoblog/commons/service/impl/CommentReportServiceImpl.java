package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.commons.entity.CommentReport;
import com.ginkgoblog.commons.mapper.CommentReportMapper;
import com.ginkgoblog.commons.service.CommentReportService;
import org.springframework.stereotype.Service;

/**
 * 评论举报表 Service 层实现类
 *
 * @author maomao
 * @date 2021-01-29
 */
@Service
public class CommentReportServiceImpl extends ServiceImpl<CommentReportMapper, CommentReport> implements CommentReportService {
}
