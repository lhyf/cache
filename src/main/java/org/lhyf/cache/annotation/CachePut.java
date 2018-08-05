package org.lhyf.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/****
 * @author YF
 * @date 2018-08-04 15:32
 * @desc CachePut
 *
 * 目标方法每次都将被调用,方法返回值将被缓存
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CachePut {

    String region() default "";

    String key();

    // 过期时间
    int expire()  default 0;

    TimeUnit timeUnit();

    // 是否缓存空值
    boolean cacheNullValue() default false;
}
