package com.saas.basic.controller;

import com.saas.basic.base.entity.SuperEntity;
import com.saas.basic.service.SuperCacheService;
import java.io.Serializable;

public abstract class SuperCacheController<S extends SuperCacheService<Id, Entity>,Id extends Serializable,Entity extends SuperEntity<Id>,SaveVO,UpdateVO,PageQuery,ResultVO> {
}
