package com.ginkgoblog.search.controller;

import com.ginkgoblog.commons.constants.MessageConf;
import com.ginkgoblog.commons.entity.Blog;
import com.ginkgoblog.commons.feign.WebFeignClient;
import com.ginkgoblog.search.constants.SysConf;
import com.ginkgoblog.search.entity.BlogIndex;
import com.ginkgoblog.search.repository.BlogRepository;
import com.ginkgoblog.search.service.ElasticSearchService;
import com.ginkgoblog.utils.ResultUtil;
import com.ginkgoblog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author maomao
 * @date 2021-02-03
 */
@RequestMapping("/search")
@Api(value = "ElasticSearch相关接口", tags = {"ElasticSearch相关接口"})
@RestController
public class ElasticSearchController {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private ElasticSearchService searchService;
    @Resource
    private WebFeignClient webFeignClient;

    @ApiOperation(value = "通过ElasticSearch搜索博客", notes = "通过ElasticSearch搜索博客", response = String.class)
    @GetMapping("/elasticSearchBlog")
    public String searchBlog(HttpServletRequest request,
                             @RequestParam(required = false) String keywords,
                             @RequestParam(name = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                             @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {

        if (StringUtils.isEmpty(keywords)) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.KEYWORD_IS_NOT_EMPTY);
        }
        return ResultUtil.result(SysConf.SUCCESS, searchService.search(keywords, currentPage, pageSize));
    }

    @ApiOperation(value = "通过uids删除ElasticSearch博客索引", notes = "通过uids删除ElasticSearch博客索引", response = String.class)
    @PostMapping("/deleteElasticSearchByUids")
    public String deleteElasticSearchByUids(@RequestParam(required = true) String uids) {

        List<String> uidList = StringUtils.changeStringToString(uids, SysConf.FILE_SEGMENTATION);
        for (String uid : uidList) {
            blogRepository.deleteById(uid);
        }
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.DELETE_SUCCESS);
    }

    @ApiOperation(value = "通过博客uid删除ElasticSearch博客索引", notes = "通过uid删除博客", response = String.class)
    @PostMapping("/deleteElasticSearchByUid")
    public String deleteElasticSearchByUid(@RequestParam(required = true) String uid) {
        blogRepository.deleteById(uid);
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.DELETE_SUCCESS);
    }

    @ApiOperation(value = "ElasticSearch通过博客Uid添加索引", notes = "添加博客", response = String.class)
    @PostMapping("/addElasticSearchIndexByUid")
    public String addElasticSearchIndexByUid(@RequestParam(required = true) String uid) {

        String result = webFeignClient.getBlogByUid(uid);

        Blog eblog = WebUtils.getData(result, Blog.class);
        if (eblog == null) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.INSERT_FAIL);
        }
        BlogIndex blog = searchService.buidBlog(eblog);
        blogRepository.save(blog);
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.INSERT_SUCCESS);
    }

    @ApiOperation(value = "ElasticSearch初始化索引", notes = "ElasticSearch初始化索引", response = String.class)
    @PostMapping("/initElasticSearchIndex")
    public String initElasticSearchIndex() throws ParseException {
        elasticsearchTemplate.deleteIndex(BlogIndex.class);
        elasticsearchTemplate.createIndex(BlogIndex.class);
        elasticsearchTemplate.putMapping(BlogIndex.class);

        Long page = 1L;
        Long row = 10L;
        Integer size = 0;

        do {

            // 查询blog信息
            String result = webFeignClient.getNewBlog(page, row);

            //构建blog
            List<Blog> blogList = WebUtils.getList(result, Blog.class);
            size = blogList.size();

            List<BlogIndex> eSBlogIndexList = blogList.stream()
                    .map(searchService::buidBlog).collect(Collectors.toList());
            //存入索引库
            blogRepository.saveAll(eSBlogIndexList);
            // 翻页
            page++;
        } while (size == 15);

        return ResultUtil.result(SysConf.SUCCESS, MessageConf.OPERATION_SUCCESS);
    }
}
