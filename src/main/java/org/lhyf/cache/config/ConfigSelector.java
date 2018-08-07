package org.lhyf.cache.config;

import org.lhyf.cache.interceptor.CacheAspect;
import org.lhyf.cache.util.RedisCacheUtils;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

/****
 * @author YF
 * @date 2018-08-04 18:16
 * @desc ConfigSelector
 *
 **/
public class ConfigSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return getProxyImports();
    }


    private String[] getProxyImports() {
        List<String> result = new ArrayList<String>();
        result.add(CacheAspect.class.getName());
        result.add(CacheConfig.class.getName());
        result.add(RedisCacheUtils.class.getName());
        return result.toArray(new String[result.size()]);
    }

}
