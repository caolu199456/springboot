package com.example.util.annotation;

import java.lang.annotation.*;

/**
 * 在这里编写说明
 *
 * @author: CL
 * @email: caolu@sunseaaiot.com
 * @date: 2019-02-28 17:49:00
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRoles {
    /**
     * 多个角色是并且关系
     * @return
     */
    String[] values() default {};

}
