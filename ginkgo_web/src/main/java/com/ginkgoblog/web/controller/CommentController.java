package com.ginkgoblog.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ginkgoblog.base.constants.MessageConstants;
import com.ginkgoblog.base.constants.SqlConstants;
import com.ginkgoblog.base.constants.SystemConstants;
import com.ginkgoblog.base.enums.ECommentSource;
import com.ginkgoblog.base.enums.ECommentType;
import com.ginkgoblog.base.enums.EStatus;
import com.ginkgoblog.commons.entity.Comment;
import com.ginkgoblog.commons.entity.User;
import com.ginkgoblog.commons.feign.PictureFeignClient;
import com.ginkgoblog.commons.service.CommentService;
import com.ginkgoblog.commons.service.UserService;
import com.ginkgoblog.commons.utils.WebUtils;
import com.ginkgoblog.commons.vo.CommentVO;
import com.ginkgoblog.commons.vo.UserVO;
import com.ginkgoblog.utils.ResultUtils;
import com.ginkgoblog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 评论 Controller 层
 *
 * @author maomao
 * @date 2021-01-27
 */
@RestController
@RequestMapping("/web/comment")
@Api("评论相关接口")
@Slf4j
public class CommentController {

    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private WebUtils webUtils;

    @ApiOperation("获取评论列表")
    @PostMapping("/getList")
    public String getList(@RequestBody CommentVO commentVO) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(commentVO.getBlogUid())) {
            queryWrapper.like(SqlConstants.BLOG_UID, commentVO.getBlogUid());
        }
        queryWrapper.eq(SqlConstants.SOURCE, commentVO.getSource());

        // 分页
        Page<Comment> page = new Page<>();
        page.setCurrent(commentVO.getCurrentPage());
        page.setSize(commentVO.getPageSize());
        queryWrapper.eq(SqlConstants.STATUS, EStatus.ENABLE);
        queryWrapper.isNull(SqlConstants.TO_UID);
        queryWrapper.orderByDesc(SqlConstants.CREATE_TIME);
        queryWrapper.eq(SqlConstants.TYPE, ECommentType.COMMENT);

        // 查询出所有的一级评论，进行分页显示
        IPage<Comment> pageList = commentService.page(page, queryWrapper);
        List<Comment> list = pageList.getRecords();
        List<String> firstUidList = new ArrayList<>();
        list.forEach(item -> firstUidList.add(item.getUid()));

        if (firstUidList.size() > 0) {
            // 查询一级评论下的子评论
            QueryWrapper<Comment> notFirstQueryWrapper = new QueryWrapper<>();
            notFirstQueryWrapper.in(SqlConstants.FIRST_COMMENT_UID, firstUidList);
            notFirstQueryWrapper.eq(SqlConstants.STATUS, EStatus.ENABLE);
            List<Comment> notFirstList = commentService.list(notFirstQueryWrapper);
            // 将子评论加入总的评论中
            if (notFirstList.size() > 0) {
                list.addAll(notFirstList);
            }
        }

        // 根据评论查询用户的相关信息
        List<String> userUidList = new ArrayList<>();
        list.forEach(item -> {
            String userUid = item.getUserUid();
            String toUserUid = item.getToUserUid();
            if (StringUtils.isNotEmpty(userUid)) {
                userUidList.add(item.getUserUid());
            }
            if (StringUtils.isNotEmpty(toUserUid)) {
                userUidList.add(item.getToUserUid());
            }
        });

        Collection<User> userList = new ArrayList<>();
        if (userUidList.size() > 0) {
            userList = userService.listByIds(userUidList);
        }

        // 过滤掉用户的敏感信息
        List<User> filterUserList = new ArrayList<>();
        userList.forEach(item -> {
            User user = new User();
            user.setAvatar(item.getAvatar());
            user.setUid(item.getUid());
            user.setNickName(item.getNickName());
            user.setUserTag(item.getUserTag());
            filterUserList.add(user);
        });

        // 获取用户头像
        StringBuffer fileUids = new StringBuffer();
        filterUserList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                fileUids.append(item.getAvatar()).append(SqlConstants.FILE_SEGMENTATION);
            }
        });

        String pictureList = pictureFeignClient.getPicture(fileUids.toString(), SqlConstants.FILE_SEGMENTATION);
        List<Map<String, Object>> picList = webUtils.getPictureMap(pictureList);
        Map<String, String> pictureMap = new HashMap<>();
        picList.forEach(item -> pictureMap.put(item.get(SqlConstants.UID).toString(),
                item.get(SqlConstants.URL).toString()));

        // key为用户uid，value为设置了头像的用户
        Map<String, User> userMap = new HashMap<>();
        filterUserList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar()) && pictureMap.get(item.getAvatar()) != null) {
                // 给用户设置头像
                item.setPhotoUrl(pictureMap.get(item.getAvatar()));
            }
            userMap.put(item.getUid(), item);
        });

        // 给所有评论设置用户信息
//        Map<String, Comment> commentMap = new HashMap<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                item.setUser(userMap.get(item.getUserUid()));
            }
            if (StringUtils.isNotEmpty(item.getToUserUid())) {
                item.setToUser(userMap.get(item.getToUserUid()));
            }
//            commentMap.put(item.getUid(), item);
        });

        // key为父评论的uid，value为子评论列表
        Map<String, List<Comment>> toCommentListMap = new HashMap<>();
        // 使用双层循环来查找父评论、子评论的关系
        for (int a = 0; a < list.size(); a++) {
            List<Comment> tempList = new ArrayList<>();
            for (int b = 0; b < list.size(); b++) {
                if (list.get(a).getUid().equals(list.get(b).getToUid())) {
                    tempList.add(list.get(b));
                }
            }
            toCommentListMap.put(list.get(a).getUid(), tempList);
        }

        List<Comment> firstComment = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isEmpty(item.getToUid())) {
                // 回复评论的uid为空，说明为一级评论
                firstComment.add(item);
            }
        });

        pageList.setRecords(getCommentReplys(firstComment, toCommentListMap));
        return ResultUtils.result(SystemConstants.SUCCESS, pageList);
    }

    /*
    @ApiOperation("获取用户的评论列表和回复")
    @PostMapping("/getListByUser")
    public String getListByUser(HttpServletRequest request, @RequestBody UserVO userVO) {
        if (request.getAttribute(SqlConstants.USER_UID) == null) {
            return ResultUtils.result(SystemConstants.ERROR, MessageConstants.INVALID_TOKEN);
        }

        String requestUserUid = request.getAttribute(SqlConstants.USER_UID).toString();
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();

        // 分页
        Page<Comment> page = new Page<>();
        page.setCurrent(userVO.getCurrentPage());
        page.setSize(userVO.getPageSize());
        queryWrapper.eq(SqlConstants.TYPE, ECommentType.COMMENT);
        queryWrapper.eq(SqlConstants.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SqlConstants.CREATE_TIME);
        // 查找出 我的评论 和 我的回复
        queryWrapper.and(wrapper -> wrapper.eq(SqlConstants.USER_UID, requestUserUid)
                .or().eq(SqlConstants.TO_USER_UID, requestUserUid));

        IPage<Comment> pageList = commentService.page(page, queryWrapper);
        List<Comment> list = pageList.getRecords();

        List<String> userUidList = new ArrayList<>();
        list.forEach(item -> {
            String userUid = item.getUserUid();
            String toUserUid = item.getToUserUid();
            if (StringUtils.isNotEmpty(userUid)) {
                userUidList.add(item.getUserUid());
            }
            if (StringUtils.isNotEmpty(toUserUid)) {
                userUidList.add(item.getToUserUid());
            }
        });

        // 获取用户列表
        Collection<User> userList = new ArrayList<>();

        if (userUidList.size() > 0) {
            userList = userService.listByIds(userUidList);
        }

        // 过滤掉用户的敏感信息
        List<User> filterUserList = new ArrayList<>();
        userList.forEach(item -> {
            User user = new User();
            user.setAvatar(item.getAvatar());
            user.setUid(item.getUid());
            user.setNickName(item.getNickName());
            filterUserList.add(user);
        });


        // 获取用户头像
        StringBuffer fileUids = new StringBuffer();
        filterUserList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                fileUids.append(item.getAvatar() + SysConf.FILE_SEGMENTATION);
            }
        });
        String pictureList = null;
        if (fileUids != null) {
            pictureList = this.pictureFeignClient.getPicture(fileUids.toString(), SysConf.FILE_SEGMENTATION);
        }
        List<Map<String, Object>> picList = webUtils.getPictureMap(pictureList);
        Map<String, String> pictureMap = new HashMap<>();
        picList.forEach(item -> {
            pictureMap.put(item.get(SQLConf.UID).toString(), item.get(SQLConf.URL).toString());
        });

        Map<String, User> userMap = new HashMap<>();
        filterUserList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar()) && pictureMap.get(item.getAvatar()) != null) {
                item.setPhotoUrl(pictureMap.get(item.getAvatar()));
            }
            userMap.put(item.getUid(), item);
        });

        // 将评论列表划分为 我的评论 和 我的回复
        List<Comment> commentList = new ArrayList<>();
        List<Comment> replyList = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                item.setUser(userMap.get(item.getUserUid()));
            }

            if (StringUtils.isNotEmpty(item.getToUserUid())) {
                item.setToUser(userMap.get(item.getToUserUid()));
            }


            // 设置sourceName
            if (StringUtils.isNotEmpty(item.getSource())) {
                item.setSourceName(ECommentSource.valueOf(item.getSource()).getName());
            }

            if (requestUserUid.equals(item.getUserUid())) {
                commentList.add(item);
            }
            if (requestUserUid.equals(item.getToUserUid())) {
                replyList.add(item);
            }
        });

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(SysConf.COMMENT_LIST, commentList);
        resultMap.put(SysConf.REPLY_LIST, replyList);

        return ResultUtils.result(SysConf.SUCCESS, resultMap);
    }
*/

    /**
     * 获取评论所有回复
     *
     * @param list             父评论列表
     * @param toCommentListMap 父评论下的所有子评论，key为父评论的uid，value为子评论列表
     * @return 设置了子评论后的父评论
     */
    private List<Comment> getCommentReplys(List<Comment> list,
                                           Map<String, List<Comment>> toCommentListMap) {
        if (list == null || list.size() == 0) {
            // 递归出口
            return new ArrayList<>();
        } else {
            list.forEach(item -> {
                // 获取父评论的uid
                String commentUid = item.getUid();
                // 根据uid获取所有的子评论
                List<Comment> replyCommentList = toCommentListMap.get(commentUid);
                // 然后再递归获取子评论的子评论
                item.setReplyList(getCommentReplys(replyCommentList, toCommentListMap));
            });
            return list;
        }
    }
}
