# 缓存穿透

当Redis查询为空的时候，设置好默认值避免高并发的请求落到数据库上即可

```
```



# 缓存击穿

# 缓存雪崩



# 分布式锁



# 基于Redis的幂等





# 缓存的Key设计

阿里开发手册中，key命名风格：

- 【推荐】 Redis key命名需具有可读性以及可管理性，不该使用含义不清的key以及特别长的key名；
- 【强制】以英文字母开头，命名中只能出现小写字母、数字、英文点号(.)和英文半角冒号(😃；
- 【强制】不要包含特殊字符，如下划线、空格、换行、单双引号以及其他转义字符；

在上面的基础上，规定了key的命名规则为：

```bash
[前缀:][租户ID:][服务模块名:]业务类型[:业务字段][:value类型][:业务值]
```

- 前缀

  可选。 用来区分不同项目或不同环境。 如：区分lamp-cloud 或 lamp-boot、lamp-cloud的dev、test或pro环境

- 租户ID

  可选。 用来区分不同租户数据缓存。 如： 租户A和租户B分别取`ContextUtil.getTenantId()`中的值来区分。

  若某个缓存需要无需区分租户，可以不设置租户ID

- 服务模块名

  可选。 用来区分不同服务或功能模块的缓存。 如： 仅在base服务使用和仅在system服务使用的缓存分别用base和system来区分，多个服务共用的缓存可以不设置或者设置为common。

- 业务类型

  必填。 用来区分不同业务类型的数据，建议设置为表名。

  当同一key涉及多个业务时， 可以使用英文半角点号 (.)分割，用来表示一个完整的语义。如：

  - lc:1:base:user.activity:id.id:number:1.1 -> [奖品id1、奖品id2]

    租户ID=1，base服务，用户ID=1，活动ID=1的奖品为奖品1（数字类型）、奖品2

  - lc:2:base:user.activity:id.id:obj:1.2 -> [奖品id1、奖品id2]

    租户ID=2，base服务，用户ID=1，活动ID=2的奖品为奖品1（数字类型）、奖品2

- 业务字段

  可选。用来区分key通过那个字段来区分，通常设置为字段名。 当同一key涉及多个业务时， 可以使用英文半角点号 (.)分割，用来对应不同业务类型的字段。如：

  - 业务类型为 user.activity 表示用户的活动，则应该用 id.id 跟 user.activity 对应，表示key中包user的id 和 activity的id

- value类型

  可选。 用来区分value存储的是完整对象、数字还是字符串。Redis key命名包含key所代表的value类型，以提高可读性。如：

  obj 表示value存储对象，number表示value存储数字， string 表示value存储字符串。

- 业务值

  可选。 用来区分同一业务类型的不同行的数据缓存。如：

  用户表id为1和i2的缓存，分别将该值设置为1和2。

## Key的定义

缓存的key实质就是一个字符串，为了统一管理key，并按照一定规律生成key，本项目封装了CacheKey和CacheHashKey来封装缓存的key。

- CacheKey

  封装redis中String、List、Set等类型的key和过期时间

- CacheHashKey

  封装redis中Hash类型的key、field和过期时间

```
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheKey {
    /**
     * redis key
     */
    @NonNull
    private String key;
    /**
     * 超时时间 秒
     */
    private Duration expire;

    public CacheKey(final @NonNull String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "key=" + key + " , expire=" + expire;
    }
}
```

```
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CacheHashKey extends CacheKey {
    /**
     * redis hash field
     */
    private Object field;

    public CacheHashKey(@NonNull String key, final Object field) {
        super(key);
        this.field = field;
    }

    public CacheHashKey(@NonNull String key, final Object field, Duration expire) {
        super(key, expire);
        this.field = field;
    }

    public CacheKey tran() {
        return new CacheKey(StrUtil.join(StrPool.COLON, getKey(), getField()), getExpire());
    }
}
```

## 如何使用

操作缓存步骤如下：

1. 定义缓存Key构造器：CacheKeyBuilder
2. 编写代码操作缓存
   1. 实现SuperCacheManager
   2. 自行调用CacheOps
   3. 自行调用CacheHashOps
   4. 自行调用RedisOps

### Key构造器

Key构造器，又名CacheKeyBuilder。为了方便生成CacheKey，可以使用CacheKeyBuilder来构造CacheKey。

```java
@FunctionalInterface
public interface CacheKeyBuilder {

    /**
     * 缓存前缀，用于区分项目，环境等等
     *
     * @return 缓存前缀
     */
    default String getPrefix() {
        return null;
    }

    /**
     * 租户编码，用于区分租户
     * <p>
     * 非租户模式设置成空字符串
     *
     * @return 租户编码
     */
    default String getTenant() {
        return ContextUtil.getTenantId() != null ? String.valueOf(ContextUtil.getTenantId()) : null;
    }

    /**
     * 设置企业id
     *
     * @param tenantId 企业id
     * @return
     */
    default CacheKeyBuilder setTenantId(Long tenantId) {
        return this;
    }

    /**
     * 服务模块名，用于区分后端服务、前端模块等
     *
     * @return 服务模块名
     */
    default String getModular() {
        return null;
    }


    /**
     * key的业务类型， 用于区分表
     *
     * @return 通常是表名
     */
    @NonNull
    String getTable();

    /**
     * key的字段名， 用于区分字段
     *
     * @return 通常是key的字段名
     */
    default String getField() {
        return null;
    }

    /**
     * 缓存的value存储的类型
     *
     * @return value类型
     */
    default ValueType getValueType() {
        return ValueType.obj;
    }

    /**
     * 缓存自动过期时间
     *
     * @return 缓存自动过期时间
     */
    @Nullable
    default Duration getExpire() {
        return null;
    }

    /**
     * 获取通配符
     *
     * @return key 前缀
     */
    default String getPattern() {
        return StrUtil.format("*:{}:*", getTable());
    }

    /**
     * 构建通用KV模式 的 cache key
     * 兼容 redis caffeine
     *
     * @param uniques 参数
     * @return cache key
     */
    default CacheKey key(Object... uniques) {
        String key = getKey(uniques);
        ArgumentAssert.notEmpty(key, "key 不能为空");
        return new CacheKey(key, getExpire());
    }

    /**
     * 构建 redis 类型的 hash cache key
     *
     * @param field   field
     * @param uniques 动态参数
     * @return cache key
     */
    default CacheHashKey hashFieldKey(@NonNull Object field, Object... uniques) {
        String key = getKey(uniques);

        ArgumentAssert.notEmpty(key, "key 不能为空");
        ArgumentAssert.notNull(field, "field 不能为空");
        return new CacheHashKey(key, field, getExpire());
    }

    /**
     * 构建 redis 类型的 hash cache key （无field)
     *
     * @param uniques 动态参数
     * @return
     */
    default CacheHashKey hashKey(Object... uniques) {
        String key = getKey(uniques);

        ArgumentAssert.notEmpty(key, "key 不能为空");
        return new CacheHashKey(key, null, getExpire());
    }

    /**
     * 根据动态参数 拼接key
     * <p>
     * key命名规范：[前缀:][租户ID:][服务模块名:]业务类型[:业务字段][:value类型][:业务值]
     *
     * @param uniques 动态参数
     * @return
     */
    default String getKey(Object... uniques) {
        ArrayList<String> regionList = new ArrayList<>();
        String prefix = this.getPrefix();
        if (StrUtil.isNotEmpty(prefix)) {
            regionList.add(prefix);
        }

        String tenant = this.getTenant();
        // 租户编码：存储默认库的全局缓存，可以重写getTenant并返回null
        if (StrUtil.isNotEmpty(tenant)) {
            regionList.add(tenant);
        }
        // 服务模块名
        String modular = getModular();
        if (StrUtil.isNotEmpty(modular)) {
            regionList.add(modular);
        }
        // 业务类型
        String table = this.getTable();
        ArgumentAssert.notEmpty(table, "缓存业务类型不能为空");
        regionList.add(table);
        // 业务字段
        String field = getField();
        if (StrUtil.isNotEmpty(field)) {
            regionList.add(field);
        }
        // value类型
        ValueType valueType = getValueType();
        if (valueType != null) {
            regionList.add(valueType.name());
        }

        // 业务值
        for (Object unique : uniques) {
            if (ObjectUtil.isNotEmpty(unique)) {
                regionList.add(String.valueOf(unique));
            }
        }
        return CollUtil.join(regionList, StrPool.COLON);
    }

    enum ValueType {
        obj, string, number,
    }
}
```

假设业务中有5个地方需要存缓存：

1. 用户完整数据：`lc:system:user:id:obj:1` -> {id: 1, name: “张三”, username: “zhangsan”}
2. 用户名 - ID：`lc:system:user:username:number:zhangsan` -> 1
3. 员工在某个活动中的奖品：`lc:123:system:employee.activity:id.id:number:1.1` -> [1,2,3]

我们可以按照下方代码来定义CacheKeyBuilder：

```java
public class UserCacheKeyBuilder implements CacheKeyBuilder {

    @Override
  	// 租户  
    public String getTenant() {
        return null;
    }

    @Override
  	// 前缀
    public String getPrefix() {
        return CacheKeyModular.PREFIX;
    }

    @Override
  	// 模块
    public String getModular() {
        return CacheKeyModular.SYSTEM;
    }

    @Override
  	// 表
    public String getTable() {
        return "user";
    }

    @Override
  	// 字段
    public String getField() {
        return SuperEntity.ID_FIELD;
    }
		
  	// 存储的value值的类型
    @Override
    public ValueType getValueType() {
        return ValueType.obj;
    }

    @Override
  	// 有效期24小时
    public Duration getExpire() {
        return Duration.ofHours(24);
    }
}
```

```
public class UserUsernameCacheKeyBuilder implements CacheKeyBuilder {

    @Override
  	// 租户  
    public String getTenant() {
        return null;
    }

    @Override
  	// 前缀
    public String getPrefix() {
        return CacheKeyModular.PREFIX;
    }

    @Override
  	// 模块
    public String getModular() {
        return CacheKeyModular.SYSTEM;
    }

    @Override
  	// 表
    public String getTable() {
        return "user";
    }

    @Override
  	// 字段
    public String getField() {
        return "username";
    }
		
  	// 存储的value值的类型
    @Override
    public ValueType getValueType() {
        return ValueType.number;
    }

    @Override
  	// 有效期24小时
    public Duration getExpire() {
        return Duration.ofHours(24);
    }
}
```

```
public class UserActivityPrizeCacheKeyBuilder implements CacheKeyBuilder {

    @Override
  	// 租户  
    public String getTenant() {
        return ContextUtil.getTenantId();
    }

    @Override
  	// 前缀
    public String getPrefix() {
        return CacheKeyModular.PREFIX;
    }

    @Override
  	// 模块
    public String getModular() {
        return CacheKeyModular.SYSTEM;
    }

    @Override
  	// 表
    public String getTable() {
        return "employee.activity";
    }

    @Override
  	// 字段
    public String getField() {
        return "id.id";
    }
		
  	// 存储的value值的类型
    @Override
    public ValueType getValueType() {
        return ValueType.number;
    }

    @Override
  	// 有效期24小时
    public Duration getExpire() {
        return Duration.ofHours(24);
    }
}
```

### 实现SuperCacheManager

调用SuperCacheController、SuperCacheService、SuperCacheManager中的部分接口时，会自动查询、设置、清理缓存值。前提是SuperCacheManager的子类需要重写`cacheKeyBuilder()`。如：

```java
@RequiredArgsConstructor
@Service
public class DefUserManagerImpl extends SuperCacheManagerImpl<DefUserMapper, DefUser> implements DefUserManager {
    @Override
    protected CacheKeyBuilder cacheKeyBuilder() {
        return new DefUserCacheKeyBuilder();
    }

}
```

SuperCacheManagerImpl 会在save、saveBatch、saveOrUpdateBatch、updateAllById、updateById、updateBatchById、removeById、removeByIds、getByIdCache等方法中操作缓存。这些方法操作的都是单条数据，操作缓存时key生成方式为：

```java
CacheKey cacheKey = cacheKeyBuilder().key(id);
cacheOps.xxx(cacheKey);
```

### 自行调用CacheOps

若SuperCacheManagerImpl封装的缓存不能满足你的需求，可以在代码中注入CacheOps来操作你的复杂业务的缓存。

```java
@RequiredArgsConstructor
@Service
public class DefUserManagerImpl extends SuperCacheManagerImpl<DefUserMapper, DefUser> implements DefUserManager {
    @Override
    protected CacheKeyBuilder cacheKeyBuilder() {
        return new DefUserCacheKeyBuilder();
    }

  	public void save2(DefUser user) {
      	CacheKey key = new XxxCacheKeyBuilder().getKey("xxx");
        cacheOps.get(key);
      	cacheOps.set(key, user);
    }
}
```

### 自行调用CacheHashOps

CacheHashOps专门用来操作hash类型的缓存，所以对应的参数都应该传入CacheHashKey。若SuperCacheManagerImpl封装的缓存不能满足你的需求，可以在代码中注入CacheHashOps来操作你的复杂业务的缓存。

```java
@RequiredArgsConstructor
@Service
public class DefUserManagerImpl extends SuperCacheManagerImpl<DefUserMapper, DefUser> implements DefUserManager {
    @Override
    protected CacheKeyBuilder cacheKeyBuilder() {
        return new DefUserCacheKeyBuilder();
    }

  	public void save3(DefUser user) {
      	CacheHashKey key = new XxxCacheKeyBuilder().hashFieldKey("field", "xxx");
        cacheOps.get(key);
      	cacheOps.set(key, user);
    }
}
```

### 自行调用RedisOps

不建议直接使用RedisOps。

若决定让项目完全依赖redis，则可以使用。使用方式同CacheOps。
