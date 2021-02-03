package com.ginkgoblog.search.repository;

import com.ginkgoblog.search.entity.BlogIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 在ElasticsearchRepository中我们可以使用Not Add Like Or Between等关键词自动创建查询语句
 *
 * @author maomao
 * @date 2021-02-03
 */
public interface BlogRepository extends ElasticsearchRepository<BlogIndex, String> {
}
