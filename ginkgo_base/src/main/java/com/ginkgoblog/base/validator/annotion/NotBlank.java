package com.ginkgoblog.base.validator.annotion;

import com.ginkgoblog.base.validator.Messages;
import com.ginkgoblog.base.validator.constraint.StringValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;


@Target({TYPE, ANNOTATION_TYPE, FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {StringValidator.class})
public @interface NotBlank {

    boolean required() default true;

    String message() default Messages.CK_NOT_BLANK_DEFAULT;

    String value() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
