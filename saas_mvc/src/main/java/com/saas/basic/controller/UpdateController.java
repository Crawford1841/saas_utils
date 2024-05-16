package com.saas.basic.controller;

import com.saas.annotation.log.WebLog;
import com.saas.basic.base.R;
import com.saas.basic.base.entity.SuperEntity;
import io.swagger.v3.oas.annotations.Operation;
import java.io.Serializable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 修改Controller
 *
 * @param <Entity>   实体
 * @param <UpdateVO> 修改参数
 */
public interface UpdateController<Id extends Serializable, Entity extends SuperEntity<Id>, UpdateVO>
        extends BaseController<Id, Entity> {

    /**
     * 修改
     *
     * @param updateVO 修改VO
     * @return 修改后的实体数据
     */
    @Operation(summary = "修改", description = "修改UpdateVO中不为空的字段")
    @PutMapping
    @WebLog(value = "'修改:' + #updateVO?.id", request = false)
    default R<Entity> update(@RequestBody @Validated(SuperEntity.Update.class) UpdateVO updateVO) {
        R<Entity> result = handlerUpdate(updateVO);
        if (result.getDefExec()) {
            return R.success(getSuperService().updateById(updateVO));
        }
        return result;
    }

    /**
     * 自定义更新
     *
     * @param updateVO 修改VO
     * @return 返回SUCCESS_RESPONSE, 调用默认更新, 返回其他不调用默认更新
     */
    default R<Entity> handlerUpdate(UpdateVO updateVO) {
        return R.successDef();
    }
}
