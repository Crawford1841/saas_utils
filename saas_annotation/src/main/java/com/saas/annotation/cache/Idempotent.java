package com.saas.annotation.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 幂等注解
 */
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /**
     * 唯一id获取方式： get/Post: key
     *
     * @return
     */
    String key() default "";

    /**
     * 需要排除的key: 一般是请求时间字段（针对有些用户1000ms内连续点击了3次）
     *
     * @return
     */
    String excludeKey() default "";

    /**
     * 幂等Token有效期：单位 秒
     *
     * @return
     */
    int seconds() default 1;

    String hint() default "";
}
