package com.ginkgoblog.web.log;

import com.ginkgoblog.base.enums.BehaviorEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志记录、自定义注解
 *
 * @author maomao
 * @date 2021-01-24
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {
    /**
     * 业务名称
     */
    String value() default "";

    /**
     * 用户行为
     */
    BehaviorEnum behavior();

    /**
     * 是否将当前日志记录到数据库中
     */
    boolean save() default true;
}
