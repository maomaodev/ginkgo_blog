package com.ginkgoblog.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ginkgoblog.commons.entity.WebVisit;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Web访问日志表 Mapper 层
 *
 * @author maomao
 * @date 2021-01-24
 */
public interface WebVisitMapper extends BaseMapper<WebVisit> {
    /**
     * 获取IP数目
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Select("SELECT COUNT(ip) FROM (SELECT ip FROM t_web_visit WHERE create_time >= #{startTime} AND create_time <= #{endTime} GROUP BY ip) AS tmp")
    Integer getIpCount(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 统计最近七天内的访问量(PV)
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Select("SELECT DISTINCT DATE_FORMAT(create_time, '%Y-%m-%d') DATE, COUNT(uid) COUNT FROM  t_web_visit where create_time >= #{startTime} AND create_time <= #{endTime} GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d')")
    List<Map<String, Object>> getPVByWeek(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 统计七天内独立用户数(UV) 目前通过IP来统计
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Select("SELECT DATE, COUNT(ip) COUNT FROM (SELECT DISTINCT DATE_FORMAT(create_time, '%Y-%m-%d') DATE, ip FROM t_web_visit WHERE create_time >= #{startTime} AND create_time <= #{endTime} GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d'),ip) AS tmp GROUP BY DATE ")
    List<Map<String, Object>> getUVByWeek(@Param("startTime") String startTime, @Param("endTime") String endTime);

}
