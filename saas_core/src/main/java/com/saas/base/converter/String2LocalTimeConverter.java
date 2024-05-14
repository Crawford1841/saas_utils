package com.saas.base.converter;

import com.saas.base.utils.DateUtils;
import java.time.LocalTime;
import org.springframework.core.convert.converter.Converter;

/**
 * 解决 @RequestParam LocalTime Date 类型的入参，参数转换问题。
 * <p>
 * HH:mm:ss
 * HH时mm分ss秒
 *
 */
public class String2LocalTimeConverter implements Converter<String, LocalTime> {
    @Override
    public LocalTime convert(String source) {
        return DateUtils.parseAsLocalTime(source);
    }
}
