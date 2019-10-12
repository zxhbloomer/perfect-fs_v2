package com.perfect.filesystem.config.annotation;

import java.lang.annotation.*;

/**
 * @author zhangxh
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SysLog {
    String value() default "";
}