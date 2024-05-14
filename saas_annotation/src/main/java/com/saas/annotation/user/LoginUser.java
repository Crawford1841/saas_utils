package com.saas.annotation.user;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginUser {
    /**
     * 是否查询SysUser对象所有信息，true则通过rpc接口查询
     */
    boolean isFull() default false;

    /**
     * 是否只查询员工信息，true则通过rpc接口查询
     */
    boolean isEmployee() default false;

    /**
     * 是否只查询用户信息，true则通过rpc接口查询
     */
    boolean isUser() default false;

    /**
     * 是否只查询角色信息，true则通过rpc接口查询
     */
    boolean isRoles() default false;


    /**
     * 是否只查询 资源 信息，true则通过rpc接口查询
     */
    boolean isResource() default false;

    /**
     * 是否只查询组织信息，true则通过rpc接口查询
     */
    boolean isOrg() default false;

    /**
     * 是否只查询主组织信息，true则通过rpc接口查询
     */
    boolean isMainOrg() default false;

    /**
     * 是否只查询岗位信息，true则通过rpc接口查询
     */
    boolean isPosition() default false;
}
