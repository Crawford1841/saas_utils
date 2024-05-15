package com.saas.annotation.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LocalCacheUpdate {

    //业务缓存前缀：区分不同业务下的相同Key
    String prefix() default "";

    //缓存key值获取方式: 请求对象.method()
    String key() default "";

    //需要通知更新缓存的模块
    String[] moduleEvents() default {};

    //默认1000ms删除缓存
    long delayTime() default 1000;
}
