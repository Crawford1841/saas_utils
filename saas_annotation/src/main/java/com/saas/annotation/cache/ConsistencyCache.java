package com.saas.annotation.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * (强一致性缓存)用于从远端缓存获取(目前只有redis,获取不到走数据库),配合@UpdateCache 主动更新缓存
 * <p>适用于允许数据短期不一致性需求</p>
 */
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConsistencyCache {

    //cache key prefix
    String prefix() default "";

    //cache key method
    String key();

    //cache clazz
    Class clazz();

    //wait one time: ms
    long waitTime() default 20;

    //max wait time: ms
    long maxWaitTime() default 200;
}
