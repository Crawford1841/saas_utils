package com.saas.annotation.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于从本地JVM缓存获取(获取不到走远端缓存或者数据库)
 * <p>适用于允许数据短期不一致性需求</p>
 */
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LocalCache {

    String prefix() default "";

    String key();

    Class clazz();

    //default expire time: s
    int expireSeconds() default 60;
}
