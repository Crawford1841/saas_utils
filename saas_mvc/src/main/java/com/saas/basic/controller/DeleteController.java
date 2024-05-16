package com.saas.basic.controller;

import com.saas.annotation.log.WebLog;
import com.saas.basic.base.R;
import com.saas.basic.base.entity.SuperEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import java.io.Serializable;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;

/**
 * 删除Controller
 * @param <Entity> 实体
 * @param <Id>     主键
 */
public interface DeleteController <Id extends Serializable,Entity extends SuperEntity<Id>> extends BaseController<Id,Entity>{


    /**
     * 删除方法
     *
     * @param ids id
     * @return 是否成功
     */
    @Operation(summary = "删除")
    @DeleteMapping
    @WebLog("'删除:' + #ids")
    default R<Boolean> delete(@RequestBody List<Id> ids) {
        R<Boolean> result = handlerDelete(ids);
        if (result.getDefExec()) {
            return R.success(getSuperService().removeByIds(ids));
        }
        return result;
    }

    /**
     * 自定义删除
     *
     * @param ids id
     * @return 返回SUCCESS_RESPONSE, 调用默认更新, 返回其他不调用默认更新
     */
    default R<Boolean> handlerDelete(List<Id> ids) {
        return R.successDef(true);
    }






}
