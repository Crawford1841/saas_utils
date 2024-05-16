package com.saas.basic.interfaces.echo;


import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 加载数据
 * <p>
 * 若一个表想要一个实体不同字段回显不同的值，可以新建多个实现类返回不一样的Map
 */
public interface LoadService {
    /**
     * 根据id查询待回显参数
     *
     * @param ids 唯一键（可能不是主键ID)
     * @return 回显数据
     */
    Map<Serializable, Object> findByIds(Set<Serializable> ids);
}
