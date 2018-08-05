package org.lhyf.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/****
 * @author YF
 * @date 2018-08-04 16:05
 * @desc Cached
 *
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cached {

    String region() default "";

    String key();

    // 过期时间
    int expire() default 0;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    // 是否缓存空值
    boolean cacheNullValue() default false;
}
