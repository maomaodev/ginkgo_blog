package com.ginkgoblog.base.validator.constraint;

import com.ginkgoblog.base.validator.annotion.Range;
import com.ginkgoblog.utils.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author maomao
 * @date 2021-01-31
 */
public class RangValidator implements ConstraintValidator<Range, String> {
    private long min;
    private long max;
    private String type;

    @Override
    public void initialize(Range constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (null == value || StringUtils.isBlank(value)) {
            return false;
        }
        return value.length() >= min && value.length() <= max;
    }
}
