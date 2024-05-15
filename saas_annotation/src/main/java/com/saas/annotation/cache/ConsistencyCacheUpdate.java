package com.saas.annotation.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConsistencyCacheUpdate {

    //cache key prefix
    String prefix() default "";

    //cache key get method
    String key() default "";
}
