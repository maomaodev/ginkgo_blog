package com.ginkgoblog.utils;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.HashMap;
import java.util.Map;

/**
 * 切面相关工具类
 *
 * @author maomao
 * @date 2021-01-24
 */
@Slf4j
public class AopUtils {
    /**
     * 获取参数名和值
     *
     * @param joinPoint
     * @return
     */
    public static Map<String, Object> getFieldsName(ProceedingJoinPoint joinPoint) throws ClassNotFoundException, NoSuchMethodException {
        // 参数值
        Object[] args = joinPoint.getArgs();

        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        String[] parameterNames = methodSignature.getParameterNames();

        // 通过map封装参数和参数值
        HashMap<String, Object> paramMap = new HashMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            paramMap.put(parameterNames[i], args[i]);
        }
        return paramMap;
    }
}
