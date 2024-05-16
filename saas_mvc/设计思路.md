MVC基础包， 封装的 Controller、Service、Manager、Mapper等，本系统借鉴了阿里的规范，采用了5层调用模型 controller -> biz -> service -> manager -> mapper, 其中biz层时可选的。

# 为什么Controller要拆分成这么多个？而Service和Mapper不拆分？

1. Controller 是给前端使用的，对于前端人员，看到的无用接口越少，越有利于对接
2. 过多的 Controller 接口暴露在外，增加被人恶意攻击风险
3. Service、Manager和Mapper都是后端人员使用，丰富一些无所谓,就算会多个后端协同开发，因为都懂JAVA，沟通阅读起来没那么难
4. Service： 业务逻辑层（大业务）， 相对具体的业务逻辑服务层
5. Manager: 通用业务层（小业务）， 继承了MP的IService：
   1. 对第三方平台封装的层，预处理返回结果及转化异常信息。
   2.  对 Service 层通用能力的下沉，如缓存方案、中间件通用处理。
   3. 与 DAO 层交互，对多个 DAO 的组合复用。
6. Mapper：数据访问层, 继承了MP的BaseMapper， 与底层MySQL交互

# controller

1. 请求转发

# biz(可选)

1. 处理不同数据源的业务逻辑。
2. 可能存在分布式事务(不能使用本地事务,即该层方法或类上不能加@Transactional)
3. 当某个业务仅仅只会操作一个具体的数据源时，可以不要biz层，直接使用service层

# service

1. 控制事务
2. 处理同一数据源的业务逻辑，

# manager

# mapper

## 总结

1. service的save方法更加贴切实际业务， manager 的save方法只负责单个表的保存操作(可以对字段进行一些默认值设置)， mapper
   的insert 方法只负责原封不动的插入数据。
2. 对于复杂的业务，避免service互相依赖
3. 调用只能从上往下，不能反着调用，最好也不要平层交叉调用。

# 具体示例

## 通用的接口层封装

```
public interface BaseController<Id extends Serializable, Entity extends SuperEntity<Id>> {

    /**
     * 获取Service
     *
     * @return Service
     */
    SuperService<Id, Entity> getSuperService();

    /**
     * 获取实体的类型
     *
     * @return 实体的类型
     */
    Class<Entity> getEntityClass();

    /**
     * 成功返回
     *
     * @param data 返回内容
     * @param <T>  返回类型
     * @return R 成功
     */
    default <T> R<T> success(T data) {
        return R.success(data);
    }

    /**
     * 成功返回
     *
     * @return R.true
     */
    default R<Boolean> success() {
        return R.success();
    }

    /**
     * 失败返回
     *
     * @param msg 失败消息
     * @param <T> 返回类型
     * @return 失败
     */
    default <T> R<T> fail(String msg) {
        return R.fail(msg);
    }

    /**
     * 失败返回
     *
     * @param msg  失败消息
     * @param args 动态参数
     * @param <T>  返回类型
     * @return 失败
     */
    default <T> R<T> fail(String msg, Object... args) {
        return R.fail(msg, args);
    }

    /**
     * 失败返回
     *
     * @param code 失败编码
     * @param msg  失败消息
     * @param <T>  返回类型
     * @return 失败
     */
    default <T> R<T> fail(int code, String msg) {
        return R.fail(code, msg);
    }

    /**
     * 失败返回
     *
     * @param exceptionCode 失败异常码
     * @return 失败
     */
    default <T> R<T> fail(BaseExceptionCode exceptionCode) {
        return R.fail(exceptionCode);
    }

    /**
     * 失败返回
     *
     * @param exception 异常
     * @return 失败
     */
    default <T> R<T> fail(BizException exception) {
        return R.fail(exception);
    }

    /**
     * 失败返回
     *
     * @param throwable 异常
     * @return 失败
     */
    default <T> R<T> fail(Throwable throwable) {
        return R.fail(throwable);
    }

    /**
     * 参数校验失败返回
     *
     * @param msg 错误消息
     * @return 失败
     */
    default <T> R<T> validFail(String msg) {
        return R.validFail(msg);
    }

    /**
     * 参数校验失败返回
     *
     * @param msg  错误消息
     * @param args 错误参数
     * @return 失败
     */
    default <T> R<T> validFail(String msg, Object... args) {
        return R.validFail(msg, args);
    }

    /**
     * 参数校验失败返回
     *
     * @param exceptionCode 错误编码
     * @return 失败
     */
    default <T> R<T> validFail(BaseExceptionCode exceptionCode) {
        return R.validFail(exceptionCode);
    }

    /**
     * 获取当前id
     *
     * @return userId
     */
    default Long getUserId() {
        return ContextUtil.getUserId();
    }

    /**
     * 当前请求租户
     *
     * @return 租户编码
     */
    default Long getTenantId() {
        return ContextUtil.getTenantId();
    }

}

```

## 业务逻辑层封装

```
public interface SuperService<Id extends Serializable, Entity extends SuperEntity<?>> {

    /**
     * 获取实体的类型
     *
     * @return 实体类class类型
     */
    Class<Entity> getEntityClass();

    /**
     * 获取主键的类型
     *
     * @return 主键class类型
     */
    Class<Id> getIdClass();

    /**
     * 获取Manager的类型
     *
     * @return Manager的class类型
     */
    SuperManager<Entity> getSuperManager();

    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param entity 实体对象
     * @return 是否插入成功
     */
    <SaveVO> Entity save(SaveVO entity);

    /**
     * 批量保存
     *
     * @param saveList 实体集合
     * @return 是否执行成功
     */
    boolean saveBatch(List<Entity> saveList);

    /**
     * 复制一条数据
     * <p>
     * 注意：若该数据存在唯一索引等限制条件，需要重写该方法进行判断或处理。
     *
     * @param id ID
     * @return 复制后的实体
     */
    Entity copy(Id id);

    /**
     * 根据 ID 修改实体中非空的字段
     *
     * @param entity 实体对象
     * @return 是否修改成功
     */
    <UpdateVO> Entity updateById(UpdateVO entity);

    /**
     * 根据id修改 entity 的所有字段
     *
     * @param entity 实体对象
     * @return 是否修改成功
     */
    <UpdateVO> Entity updateAllById(UpdateVO entity);

    /**
     * 删除（根据ID 批量删除）
     *
     * @param idList 主键ID列表
     * @return 是否删除成功
     */
    boolean removeByIds(Collection<Id> idList);

    /**
     * 根据 ID 查询
     *
     * @param id 主键ID
     * @return 实体对象或null
     */
    Entity getById(Id id);

    /**
     * 查询列表
     *
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     * @return 实体对象集合或空集合
     */
    List<Entity> list(Wrapper<Entity> queryWrapper);

    /**
     * 批量查询
     *
     * @param ids 主键
     * @return 实体对象集合或空集合
     */
    List<Entity> listByIds(List<Id> ids);


    /**
     * 翻页查询
     *
     * @param page         翻页对象
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     * @return 实体分页对象
     */
    <E extends IPage<Entity>> E page(E page, Wrapper<Entity> queryWrapper);
}
```

## 通用的业务层

```
/**
 * 基于MP的 IService 新增了2个方法： saveBatchSomeColumn、updateAllById
 * 其中：
 * 1，updateAllById 执行后，会清除缓存
 * 2，saveBatchSomeColumn 批量插入
 *
 * @param <T> 实体
 */
public interface SuperManager<T> extends IService<T> {
    /**
     * 获取实体的类型
     *
     * @return
     */
    @Override
    Class<T> getEntityClass();

    /**
     * 批量保存数据
     * <p>
     * 注意：该方法仅仅测试过mysql
     *
     * @param entityList
     * @return
     */
    default boolean saveBatchSomeColumn(List<T> entityList) {
        if (entityList.isEmpty()) {
            return true;
        }
        if (entityList.size() > 5000) {
            throw BizException.wrap(ExceptionCode.TOO_MUCH_DATA_ERROR);
        }
        return SqlHelper.retBool(((SuperMapper) getBaseMapper()).insertBatchSomeColumn(entityList));
    }

    /**
     * 根据id修改 entity 的所有字段
     *
     * @param entity
     * @return
     */
    boolean updateAllById(T entity);

}

```

## 数据访问层

```
/**
 * 基于MP的 BaseMapper 新增了2个方法： insertBatchSomeColumn、updateAllById
 *
 * @param <T> 实体
 */
public interface SuperMapper<T> extends BaseMapper<T> {
    /**
     * 全量修改所有字段
     *
     * @param entity 实体
     * @return 修改数量
     */
    int updateAllById(@Param(Constants.ENTITY) T entity);

    /**
     * 批量插入所有字段
     * <p>
     * 只测试过MySQL！只测试过MySQL！只测试过MySQL！
     *
     * @param entityList 实体集合
     * @return 插入数量
     */
    int insertBatchSomeColumn(List<T> entityList);
}
```

