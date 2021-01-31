package com.ginkgoblog.base.validator.constraint;

import com.ginkgoblog.base.validator.annotion.NotBlank;
import com.ginkgoblog.utils.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 不为空字符串
 *
 * @author maomao
 * @date 2021-01-31
 */
public class StringValidator implements ConstraintValidator<NotBlank, String> {
    @Override
    public void initialize(NotBlank constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || StringUtils.isBlank(value) || StringUtils.isEmpty(value.trim())) {
            return false;
        }
        return true;
    }
}
