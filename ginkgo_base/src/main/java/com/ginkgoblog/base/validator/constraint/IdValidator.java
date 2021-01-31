package com.ginkgoblog.base.validator.constraint;

import com.ginkgoblog.base.constants.Constants;
import com.ginkgoblog.base.validator.annotion.IdValid;
import com.ginkgoblog.utils.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 这里有些冗余了，其实面对控制器的VO对象，应该全为String类型。
 * 作为后端程序员，不应该相信前端传递的任何参数，所以字符串类型也应该被识别。
 *
 * @author maomao
 * @date 2021-01-31
 */
public class IdValidator implements ConstraintValidator<IdValid, String> {


    @Override
    public void initialize(IdValid constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || StringUtils.isBlank(value) || StringUtils.isEmpty(value.trim()) || value.length() != Constants.THIRTY_TWO) {
            return false;
        }
        return true;
    }
}
