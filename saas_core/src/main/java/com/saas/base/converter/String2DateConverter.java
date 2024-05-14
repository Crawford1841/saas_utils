package com.saas.base.converter;

import com.saas.base.utils.DateUtils;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

/**
 * 解决 @RequestParam 标记的 Date 类型的入参，参数转换问题。
 * <p>
 * yyyy
 * yyyy-MM
 * yyyy-MM-dd
 * yyyy-MM-dd HH
 * yyyy-MM-dd HH:mm
 * yyyy-MM-dd HH:mm:ss
 * yyyy/MM
 * yyyy/MM/dd
 * yyyy/MM/dd HH
 * yyyy/MM/dd HH:mm
 * yyyy/MM/dd HH:mm:ss
 * yyyy年MM月
 * yyyy年MM月dd日
 * yyyy年MM月dd日HH时mm分ss秒
 *
 */
@Slf4j
public class String2DateConverter implements Converter<String, Date> {

    @Override
    @Nullable
    public Date convert(String source) {
        return DateUtils.parseAsDate(source);
    }

}
