package com.saas.basic.controller;

import cn.hutool.core.util.URLUtil;
import jakarta.servlet.http.HttpServletResponse;
import com.saas.basic.base.entity.SuperEntity;
import java.io.IOException;
import java.io.Serializable;
import lombok.SneakyThrows;

/**
 * 删除Controller
 *
 * @param <Entity> 实体
 * @param <Id>     主键
 */
public interface DownloadController<Id extends Serializable, Entity extends SuperEntity<Id>>
        extends BaseController<Id, Entity> {

    /**
     * 生成zip文件
     *
     * @param data     数据
     * @param fileName 文件名
     * @param response 响应
     * @throws IOException
     */
    @SneakyThrows
    default void write(byte[] data, String fileName, HttpServletResponse response) {
        response.reset();
        response.setHeader("Content-Disposition", "attachment;filename=" + URLUtil.encode(fileName));
        response.addHeader("Content-Length", String.valueOf(data.length));
        response.setContentType("application/octet-stream; charset=UTF-8");
        response.getOutputStream().write(data);
    }


}
