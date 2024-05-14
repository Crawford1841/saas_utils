package com.saas.base.funtion;
/**
 * 处理异常的 函数
 *
 */
@FunctionalInterface
public interface CheckedFunction<T,R> {
    /**
     * 执行
     *
     * @param t 入参
     * @return R 出参
     * @throws Exception 异常
     */
    R apply(T t) throws Exception;
}
