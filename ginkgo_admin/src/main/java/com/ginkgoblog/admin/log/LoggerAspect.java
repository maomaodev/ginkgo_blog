package com.ginkgoblog.admin.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ginkgoblog.base.constants.RedisConstants;
import com.ginkgoblog.base.holder.RequestHolder;
import com.ginkgoblog.base.utils.RequestUtil;
import com.ginkgoblog.commons.entity.ExceptionLog;
import com.ginkgoblog.utils.AopUtils;
import com.ginkgoblog.utils.AspectUtils;
import com.ginkgoblog.utils.IpUtils;
import com.ginkgoblog.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 日志切面
 *
 * @author maomao
 * @date 2021-01-27
 */
@Aspect
@Component
@Slf4j
public class LoggerAspect {

    @Autowired
    private SysLogHandle sysLogHandle;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 开始时间
     */
    Date startTime;

    @Pointcut(value = "@annotation(operationLogger)")
    public void pointcut(OperationLogger operationLogger) {

    }

    @Around(value = "pointcut(operationLogger)")
    public Object doAround(ProceedingJoinPoint joinPoint, OperationLogger operationLogger) throws Throwable {

        startTime = new Date();

        //先执行业务
        Object result = joinPoint.proceed();

        try {
            // 日志收集
            handle(joinPoint);

        } catch (Exception e) {
            log.error("日志记录出错!", e);
        }

        return result;
    }


    @AfterThrowing(value = "pointcut(operationLogger)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, OperationLogger operationLogger, Throwable e) throws Exception {

        ExceptionLog exception = new ExceptionLog();
        HttpServletRequest request = RequestHolder.getRequest();
        String ip = IpUtils.getIpAddr(request);
        exception.setIp(ip);
        String operationName = AspectUtils.INSTANCE.parseParams(joinPoint.getArgs(), operationLogger.value());

        //从Redis中获取IP来源
        String jsonResult = redisTemplate.opsForValue().get(
                RedisConstants.IP_SOURCE + RedisConstants.SEGMENTATION + ip);
        if (StringUtils.isEmpty(jsonResult)) {
            String addresses = IpUtils.getAddresses(RedisConstants.IP + RedisConstants.EQUAL_TO + ip, RedisConstants.UTF_8);
            if (StringUtils.isNotEmpty(addresses)) {
                exception.setIpSource(addresses);
                redisTemplate.opsForValue().set(RedisConstants.IP_SOURCE + RedisConstants.SEGMENTATION + ip, addresses, 24, TimeUnit.HOURS);
            }
        } else {
            exception.setIpSource(jsonResult);
        }

        //设置请求信息
        exception.setIp(ip);

        //设置调用的方法
        exception.setMethod(joinPoint.getSignature().getName());

        exception.setExceptionJson(JSON.toJSONString(e,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteMapNullValue));
        exception.setExceptionMessage(e.getMessage());

        exception.setOperation(operationName);
        exception.setCreateTime(new Date());
        exception.setUpdateTime(new Date());

        exception.insert();
    }


    /**
     * 管理员日志收集
     *
     * @param point
     * @throws Exception
     */
    private void handle(ProceedingJoinPoint point) throws Exception {

        HttpServletRequest request = RequestHolder.getRequest();
        Method currentMethod = AspectUtils.INSTANCE.getMethod(point);

        //获取操作名称
        OperationLogger annotation = currentMethod.getAnnotation(OperationLogger.class);

        boolean save = annotation.save();

        String bussinessName = AspectUtils.INSTANCE.parseParams(point.getArgs(), annotation.value());

        String ua = RequestUtil.getUa();

        log.info("{} | {} - {} {} - {}", bussinessName, IpUtils.getIpAddr(request), RequestUtil.getMethod(), RequestUtil.getRequestUrl(), ua);
        if (!save) {
            return;
        }

        // 获取参数名称和值
        Map<String, Object> nameAndArgsMap = AopUtils.getFieldsName(point);
        String paramsJson = JSON.toJSONString(nameAndArgsMap);

        // 异步存储日志
        sysLogHandle.setSysLogHandle(paramsJson, point.getTarget().getClass().getName(), point.getSignature().getName(), bussinessName, startTime);
        sysLogHandle.onRun();
    }
}
