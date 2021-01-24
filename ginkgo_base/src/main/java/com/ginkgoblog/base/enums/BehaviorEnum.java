package com.ginkgoblog.base.enums;

import com.alibaba.fastjson.JSON;
import com.ginkgoblog.base.constants.SqlConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户行为枚举类
 *
 * @author maomao
 * @date 2021-01-24
 */
public enum BehaviorEnum {

    BLOG_TAG("点击标签", "blog_tag"),
    BLOG_SORT("点击博客分类", "blog_sort"),
    BLOG_CONTENT("点击博客", "blog_content"),
    BLOG_PRAISE("点赞", "blog_praise"),
    FRIENDSHIP_LINK("点击友情链接", "friendship_link"),
    BLOG_SEARCH("点击搜索", "blog_search"),
    STUDY_VIDEO("点击学习视频", "study_video"),
    VISIT_PAGE("访问页面", "visit_page"),
    VISIT_SORT("点击归档", "visit_sort"),
    BLOG_AUTHOR("点击作者", "blog_author"),
    PUBLISH_COMMENT("发表评论", "publish_comment"),
    DELETE_COMMENT("删除评论", "delete_comment"),
    REPORT_COMMENT("举报评论", "report_comment"),
    VISIT_CLASSIFY("点击博客分类页面", "visit_classify"),
    VISIT_TAG("点击博客标签页面", "visit_tag");

    private String content;
    private String behavior;

    BehaviorEnum(String content, String behavior) {
        this.content = content;
        this.behavior = behavior;
    }

    public static Map<String, String> getModuleAndOtherData(BehaviorEnum behavior, Map<String, Object> nameAndArgsMap, String bussinessName) {
        String otherData = "";
        String moduleUid = "";
        switch (behavior) {
            case BLOG_AUTHOR: {
                // 判断是否是点击作者
                if (nameAndArgsMap.get(SqlConstants.AUTHOR) != null) {
                    otherData = nameAndArgsMap.get(SqlConstants.AUTHOR).toString();
                }
            }
            ;
            break;
            case BLOG_SORT: {
                // 判断是否点击博客分类
                if (nameAndArgsMap.get(SqlConstants.BLOG_SORT_UID) != null) {
                    moduleUid = nameAndArgsMap.get(SqlConstants.BLOG_SORT_UID).toString();
                }
            }
            ;
            break;
            case BLOG_TAG: {
                // 判断是否点击博客标签
                if (nameAndArgsMap.get(SqlConstants.TAG_UID) != null) {
                    moduleUid = nameAndArgsMap.get(SqlConstants.TAG_UID).toString();
                }
            }
            ;
            break;
            case BLOG_SEARCH: {
                // 判断是否进行搜索
                if (nameAndArgsMap.get(SqlConstants.KEYWORDS) != null) {
                    otherData = nameAndArgsMap.get(SqlConstants.KEYWORDS).toString();
                }
            }
            ;
            break;
            case VISIT_CLASSIFY: {
                // 判断是否点击分类
                if (nameAndArgsMap.get(SqlConstants.BLOG_SORT_UID) != null) {
                    moduleUid = nameAndArgsMap.get(SqlConstants.BLOG_SORT_UID).toString();
                }
            }
            ;
            break;
            case VISIT_SORT: {
                // 判断是否点击归档
                if (nameAndArgsMap.get(SqlConstants.MONTH_DATE) != null) {
                    otherData = nameAndArgsMap.get(SqlConstants.MONTH_DATE).toString();
                }
            }
            ;
            break;
            case BLOG_CONTENT: {
                // 判断是否博客详情
                if (nameAndArgsMap.get(SqlConstants.UID) != null) {
                    moduleUid = nameAndArgsMap.get(SqlConstants.UID).toString();
                }
            }
            ;
            break;
            case BLOG_PRAISE: {
                // 判断是否给博客点赞
                if (nameAndArgsMap.get(SqlConstants.UID) != null) {
                    moduleUid = nameAndArgsMap.get(SqlConstants.UID).toString();
                }
            }
            ;
            break;
            case FRIENDSHIP_LINK: {
                // 判断是否点击友链
                if (nameAndArgsMap.get(SqlConstants.UID) != null) {
                    moduleUid = nameAndArgsMap.get(SqlConstants.UID).toString();
                }
                otherData = bussinessName;
            }
            ;
            break;
            case VISIT_PAGE: {
                // 访问页面
                if (nameAndArgsMap.get(SqlConstants.PAGE_NAME) != null) {
                    otherData = nameAndArgsMap.get(SqlConstants.PAGE_NAME).toString();
                } else {
                    otherData = bussinessName;
                }
            }
            ;
            break;
            case PUBLISH_COMMENT: {
                Object object = nameAndArgsMap.get(SqlConstants.COMMENT_VO);
                Map<String, Object> map = (Map<String, Object>) JSON.parse(JSON.toJSONString(object));
                if (map.get(SqlConstants.CONTENT) != null) {
                    otherData = map.get(SqlConstants.CONTENT).toString();
                }
            }
            ;
            break;
            case REPORT_COMMENT: {
                // 举报评论
                Object object = nameAndArgsMap.get(SqlConstants.COMMENT_VO);
                Map<String, Object> map = (Map<String, Object>) JSON.parse(JSON.toJSONString(object));
                if (map.get(SqlConstants.CONTENT) != null) {
                    otherData = map.get(SqlConstants.CONTENT).toString();
                }
            }
            ;
            break;
            case DELETE_COMMENT: {
                // 删除评论
                Object object = nameAndArgsMap.get(SqlConstants.COMMENT_VO);
                Map<String, Object> map = (Map<String, Object>) JSON.parse(JSON.toJSONString(object));
                if (map.get(SqlConstants.CONTENT) != null) {
                    otherData = map.get(SqlConstants.CONTENT).toString();
                }
            }
            ;
            break;
            default:
                break;
        }
        Map<String, String> result = new HashMap<>();
        result.put(SqlConstants.MODULE_UID, moduleUid);
        result.put(SqlConstants.OTHER_DATA, otherData);
        return result;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }
}
