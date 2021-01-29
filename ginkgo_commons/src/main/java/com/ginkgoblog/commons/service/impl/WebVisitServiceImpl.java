package com.ginkgoblog.commons.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ginkgoblog.base.constants.BaseSysConf;
import com.ginkgoblog.base.enums.EBehavior;
import com.ginkgoblog.base.enums.EStatus;
import com.ginkgoblog.commons.constants.SQLConf;
import com.ginkgoblog.commons.constants.SysConf;
import com.ginkgoblog.commons.entity.*;
import com.ginkgoblog.commons.mapper.WebVisitMapper;
import com.ginkgoblog.commons.service.*;
import com.ginkgoblog.commons.vo.WebVisitVO;
import com.ginkgoblog.utils.DateUtils;
import com.ginkgoblog.utils.IpUtils;
import com.ginkgoblog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Web访问日志表 Service 层实现类
 *
 * @author maomao
 * @date 2021-01-24
 */
public class WebVisitServiceImpl extends ServiceImpl<WebVisitMapper, WebVisit> implements WebVisitService {

    @Resource
    private WebVisitMapper webVisitMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private LinkService linkService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private BlogSortService blogSortService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Async
    @Override
    public void addWebVisit(String userUid, HttpServletRequest request, String behavior, String moduleUid, String otherData) {

        //增加记录（可以考虑使用AOP）
        Map<String, String> map = IpUtils.getOsAndBrowserInfo(request);
        String os = map.get("OS");
        String browser = map.get("BROWSER");
        WebVisit webVisit = new WebVisit();
        String ip = IpUtils.getIpAddr(request);
        webVisit.setIp(ip);

        //从Redis中获取IP来源
        String jsonResult = stringRedisTemplate.opsForValue().get("IP_SOURCE:" + ip);
        if (StringUtils.isEmpty(jsonResult)) {
            String addresses = IpUtils.getAddresses("ip=" + ip, "utf-8");
            if (StringUtils.isNotEmpty(addresses)) {
                webVisit.setIpSource(addresses);
                stringRedisTemplate.opsForValue().set("IP_SOURCE" + BaseSysConf.REDIS_SEGMENTATION + ip, addresses, 24, TimeUnit.HOURS);
            }
        } else {
            webVisit.setIpSource(jsonResult);
        }
        webVisit.setOs(os);
        webVisit.setBrowser(browser);
        webVisit.setUserUid(userUid);
        webVisit.setBehavior(behavior);
        webVisit.setModuleUid(moduleUid);
        webVisit.setOtherData(otherData);
        webVisit.insert();
    }

    @Override
    public int getWebVisitCount() {

        // 获取今日开始和结束时间
        String startTime = DateUtils.getToDayStartTime();
        String endTime = DateUtils.getToDayEndTime();
        return webVisitMapper.getIpCount(startTime, endTime);
    }

    @Override
    public Map<String, Object> getVisitByWeek() {

        // 获取到今天结束的时间
        String todayEndTime = DateUtils.getToDayEndTime();

        //获取最近七天的日期
        Date sevenDaysDate = DateUtils.getDate(todayEndTime, -6);

        String sevenDays = DateUtils.getOneDayStartTime(sevenDaysDate);

        // 获取最近七天的数组列表
        List<String> sevenDaysList = DateUtils.getDaysByN(7, "yyyy-MM-dd");
        // 获得最近七天的访问量
        List<Map<String, Object>> pvMap = webVisitMapper.getPVByWeek(sevenDays, todayEndTime);
        // 获得最近七天的独立用户
        List<Map<String, Object>> uvMap = webVisitMapper.getUVByWeek(sevenDays, todayEndTime);

        Map<String, Object> countPVMap = new HashMap<>();
        Map<String, Object> countUVMap = new HashMap<>();

        for (Map<String, Object> item : pvMap) {
            countPVMap.put(item.get("DATE").toString(), item.get("COUNT"));
        }
        for (Map<String, Object> item : uvMap) {
            countUVMap.put(item.get("DATE").toString(), item.get("COUNT"));
        }
        // 访问量数组
        List<Integer> pvList = new ArrayList<>();
        // 独立用户数组
        List<Integer> uvList = new ArrayList<>();

        for (String day : sevenDaysList) {
            if (countPVMap.get(day) != null) {
                Number pvNumber = (Number) countPVMap.get(day);
                Number uvNumber = (Number) countUVMap.get(day);
                pvList.add(pvNumber.intValue());
                uvList.add(uvNumber.intValue());
            } else {
                pvList.add(0);
                uvList.add(0);
            }
        }

        Map<String, Object> resultMap = new HashMap<>();

        // 不含年份的数组格式
        List<String> resultSevenDaysList = DateUtils.getDaysByN(7, "MM-dd");

        resultMap.put("date", resultSevenDaysList);
        resultMap.put("pv", pvList);
        resultMap.put("uv", uvList);

        return resultMap;
    }

    @Override
    public IPage<WebVisit> getPageList(WebVisitVO webVisitVO) {
        QueryWrapper<WebVisit> queryWrapper = new QueryWrapper<>();

        // 得到所有的枚举对象
        EBehavior[] arr = EBehavior.values();

        // 设置关键字查询
        if (!StringUtils.isEmpty(webVisitVO.getKeyword()) && !StringUtils.isEmpty(webVisitVO.getKeyword().trim())) {

            String behavior = "";
            for (int a = 0; a < arr.length; a++) {
                // 设置行为名称
                if (arr[a].getContent().equals(webVisitVO.getKeyword().trim())) {
                    behavior = arr[a].getBehavior();
                }
            }

            queryWrapper.like(SQLConf.IP, webVisitVO.getKeyword().trim()).or().eq(SQLConf.BEHAVIOR, behavior);
        }

        // 设置起始时间段
        if (!StringUtils.isEmpty(webVisitVO.getStartTime())) {
            String[] time = webVisitVO.getStartTime().split(SysConf.FILE_SEGMENTATION);
            if (time.length == 2) {
                queryWrapper.between(SQLConf.CREATE_TIME, DateUtils.str2Date(time[0]), DateUtils.str2Date(time[1]));
            }
        }

        Page<WebVisit> page = new Page<>();
        page.setCurrent(webVisitVO.getCurrentPage());
        page.setSize(webVisitVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        IPage<WebVisit> pageList = webVisitMapper.selectPage(page, queryWrapper);

        List<WebVisit> list = pageList.getRecords();
        List<String> blogUids = new ArrayList<>();
        List<String> tagUids = new ArrayList<>();
        List<String> sortUids = new ArrayList<>();
        List<String> linkUids = new ArrayList<>();

        list.forEach(item -> {
            if (item.getBehavior().equals(EBehavior.BLOG_CONTENT.getBehavior())) {
                blogUids.add(item.getModuleUid());
            } else if (item.getBehavior().equals(EBehavior.BLOG_SORT.getBehavior())) {
                sortUids.add(item.getModuleUid());
            } else if (item.getBehavior().equals(EBehavior.BLOG_TAG.getBehavior())) {
                tagUids.add(item.getModuleUid());
            } else if (item.getBehavior().equals(EBehavior.FRIENDSHIP_LINK.getBehavior())) {
                linkUids.add(item.getModuleUid());
            }
        });
        Collection<Blog> blogList = new ArrayList<>();
        Collection<Tag> tagList = new ArrayList<>();
        Collection<BlogSort> sortList = new ArrayList<>();
        Collection<Link> linkList = new ArrayList<>();

        if (blogUids.size() > 0) {
            blogList = blogService.listByIds(blogUids);
        }

        if (tagUids.size() > 0) {
            tagList = tagService.listByIds(tagUids);
        }

        if (sortUids.size() > 0) {
            sortList = blogSortService.listByIds(sortUids);
        }

        if (linkUids.size() > 0) {
            linkList = linkService.listByIds(linkUids);
        }

        Map<String, String> contentMap = new HashMap<>();
        blogList.forEach(item -> {
            contentMap.put(item.getUid(), item.getTitle());
        });

        tagList.forEach(item -> {
            contentMap.put(item.getUid(), item.getContent());
        });

        sortList.forEach(item -> {
            contentMap.put(item.getUid(), item.getContent());
        });

        linkList.forEach(item -> {
            contentMap.put(item.getUid(), item.getTitle());
        });

        list.forEach(item -> {

            for (int a = 0; a < arr.length; a++) {
                // 设置行为名称
                if (arr[a].getBehavior().equals(item.getBehavior())) {
                    item.setBehaviorContent(arr[a].getContent());
                    break;
                }
            }

            if (item.getBehavior().equals(EBehavior.BLOG_CONTENT.getBehavior()) ||
                    item.getBehavior().equals(EBehavior.BLOG_SORT.getBehavior()) ||
                    item.getBehavior().equals(EBehavior.BLOG_TAG.getBehavior()) ||
                    item.getBehavior().equals(EBehavior.FRIENDSHIP_LINK.getBehavior())) {

                //从map中获取到对应的名称
                if (StringUtils.isNotEmpty(item.getModuleUid())) {
                    item.setContent(contentMap.get(item.getModuleUid()));
                }

            } else {
                item.setContent(item.getOtherData());
            }
        });
        pageList.setRecords(list);
        return pageList;
    }
}
