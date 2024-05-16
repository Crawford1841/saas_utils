package com.saas.basic.interfaces.echo;

import java.util.Map;

/**
 * 注入VO 父类
 */
public interface EchoVO {

    /**
     * 回显值 集合
     *
     * @return 回显值 集合
     */
    Map<String, Object> getEchoMap();
}
