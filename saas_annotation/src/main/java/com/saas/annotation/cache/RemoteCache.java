package com.saas.annotation.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * (非强一致性缓存)用于从远端缓存获取(目前只有redis,获取不到走数据库),配合@RemoteCacheUpdate 主动更新缓存
 * <p>适用于允许数据短期不一致性需求</p>
 */
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RemoteCache {

    //cache prefix
    String prefix() default "";

    //cache key
    String key();

    //cache clazz
    Class clazz();

    //default expire time: s
    int expireSeconds() default 60;
}
