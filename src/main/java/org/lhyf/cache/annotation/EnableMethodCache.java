package org.lhyf.cache.annotation;

import org.lhyf.cache.config.ConfigSelector;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/****
 * @author YF
 * @date 2018-08-04 16:49
 * @desc EnableMethodCache
 *
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableAspectJAutoProxy
@Import(ConfigSelector.class)
public @interface EnableMethodCache {
    String region() default "";
    // 过期时间
    int expire() default 3600;

    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
