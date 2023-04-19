package com.hua.mall.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述：权限校验
 *
 * @author hua
 * @date 2022/10/25 13:54
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 必须拥有一个角色
     *
     * @return
     */
    String mustRole() default "";
}
