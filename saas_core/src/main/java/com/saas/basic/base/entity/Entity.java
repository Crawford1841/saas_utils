package com.saas.basic.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 包括id、created_time、created_by、updated_by、updated_time字段的表继承的基础实体
 *
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Entity<T> extends SuperEntity<T>{
    public static final String UPDATED_TIME = "updatedTime";
    public static final String UPDATED_BY = "updatedBy";
    public static final String UPDATED_TIME_FIELD = "updated_time";
    public static final String UPDATED_BY_FIELD = "updated_by";
    private static final long serialVersionUID = 5169873634279173683L;

    @Schema(description = "最后修改时间")
    @TableField(value = UPDATED_TIME_FIELD, fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime updatedTime;

    @Schema(description = "最后修改人ID")
    @TableField(value = UPDATED_BY_FIELD, fill = FieldFill.INSERT_UPDATE)
    protected T updatedBy;

    public Entity(T id, LocalDateTime createdTime, T createdBy, LocalDateTime updatedTime, T updatedBy) {
        super(id, createdTime, createdBy);
        this.updatedTime = updatedTime;
        this.updatedBy = updatedBy;
    }

    public Entity() {
    }
}
