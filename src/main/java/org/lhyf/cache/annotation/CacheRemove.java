package org.lhyf.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/****
 * @author YF
 * @date 2018-08-04 16:07
 * @desc CacheEvict
 *
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheRemove {

    String region() default "";
    String[] key();
    boolean allKey() default false;

}
