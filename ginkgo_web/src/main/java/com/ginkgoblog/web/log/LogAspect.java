package com.ginkgoblog.web.log;

import com.ginkgoblog.base.constants.SqlConstants;
import com.ginkgoblog.base.enums.EBehavior;
import com.ginkgoblog.base.holder.RequestHolder;
import com.ginkgoblog.base.utils.RequestUtil;
import com.ginkgoblog.utils.AopUtils;
import com.ginkgoblog.utils.AspectUtils;
import com.ginkgoblog.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 日志切面
 *
 * @author maomao
 * @date 2021-01-24
 */
@Aspect
@Component("WebLoggerAspect")
@Slf4j
public class LogAspect {
    @Autowired
    private SysLogHandle sysLogHandle;

    @Pointcut(value = "@annotation(operationLog)")
    public void pointcut(OperationLog operationLog) {
    }

    @Around(value = "pointcut(operationLog)")
    public Object doAround(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
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

    private void handle(ProceedingJoinPoint point) throws Exception {

        HttpServletRequest request = RequestHolder.getRequest();
        Method currentMethod = AspectUtils.INSTANCE.getMethod(point);
        //获取操作名称
        OperationLog annotation = currentMethod.getAnnotation(OperationLog.class);

        boolean save = annotation.save();
        EBehavior behavior = annotation.behavior();
        String operationName = AspectUtils.INSTANCE.parseParams(point.getArgs(), annotation.value());
        String ua = RequestUtil.getUa();

        log.info("{} | {} - {} {} - {}", operationName, IpUtils.getIpAddr(request),
                RequestUtil.getMethod(), RequestUtil.getRequestUrl(), ua);
        if (!save) {
            return;
        }

        // 获取参数名称和值
        Map<String, Object> nameAndArgsMap = AopUtils.getFieldsName(point);
        Map<String, String> result = EBehavior.getModuleAndOtherData(behavior, nameAndArgsMap, operationName);

        String userUid = "";
        if (request.getAttribute(SqlConstants.USER_UID) != null) {
            userUid = request.getAttribute(SqlConstants.USER_UID).toString();
        }
        // 异步存储日志
        sysLogHandle.setSysLogHandle(userUid, behavior.getBehavior(),
                result.get(SqlConstants.MODULE_UID), result.get(SqlConstants.OTHER_DATA));
        sysLogHandle.onRun();
    }
}
