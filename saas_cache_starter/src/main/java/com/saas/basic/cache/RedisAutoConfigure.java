package com.saas.basic.cache;

import com.saas.basic.cache.properties.CustomCacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Redis配置类
 */
@ConditionalOnClass(RedisConnectionFactory.class)
@ConditionalOnProperty(prefix = CustomCacheProperties.PREFIX)
public class RedisAutoConfigure {
}
