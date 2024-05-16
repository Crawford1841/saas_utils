package com.saas.basic.controller;

import com.saas.annotation.log.WebLog;
import com.saas.basic.base.R;
import com.saas.basic.base.entity.SuperEntity;
import io.swagger.v3.oas.annotations.Operation;
import java.io.Serializable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 新增
 *
 * @param <Entity> 实体
 * @param <SaveVO> 保存参数
 */
public interface SaveController<Id extends Serializable, Entity extends SuperEntity<Id>, SaveVO>
        extends BaseController<Id, Entity> {

    /**
     * 新增
     *
     * @param saveVO 保存参数
     * @return 实体
     */
    @Operation(summary = "新增")
    @PostMapping
    @WebLog(value = "新增", request = false)
    default R<Entity> save(@RequestBody @Validated SaveVO saveVO) {
        R<Entity> result = handlerSave(saveVO);
        if (result.getDefExec()) {
            return R.success(getSuperService().save(saveVO));
        }
        return result;
    }

    /**
     * 复制
     *
     * @param id ID
     * @return 实体
     */
    @Operation(summary = "复制")
    @PostMapping("/copy")
    @WebLog(value = "复制", request = false)
    default R<Entity> copy(@RequestParam("id") Id id) {
        return R.success(getSuperService().copy(id));
    }

    /**
     * 自定义新增
     *
     * @param model 保存对象
     * @return 返回SUCCESS_RESPONSE, 调用默认更新, 返回其他不调用默认更新
     */
    default R<Entity> handlerSave(SaveVO model) {
        return R.successDef();
    }

}
