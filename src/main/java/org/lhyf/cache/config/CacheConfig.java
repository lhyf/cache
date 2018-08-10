package org.lhyf.cache.config;

import org.lhyf.cache.annotation.EnableMethodCache;
import org.lhyf.cache.serializer.MsgPackSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.concurrent.TimeUnit;

/****
 * @author YF
 * @date 2018-08-04 18:11
 * @desc CacheConfig
 *
 **/
@Configuration
public class CacheConfig implements ImportAware {

    protected AnnotationAttributes enableMethodCache;
    private static String defaultRegion;
    private static int defaultExpire;
    private static TimeUnit defaultTimeUnit;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableMethodCache = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableMethodCache.class.getName(), false));
    }

    @Primary
    @Bean
    public ConfigMap configMap(){
        ConfigMap map = new ConfigMap();
        defaultRegion = this.enableMethodCache.getString("region");
        defaultExpire = this.enableMethodCache.getNumber("expire");
        defaultTimeUnit = (TimeUnit) this.enableMethodCache.get("timeUnit");

        map.getMap().put("region",defaultRegion);
        map.getMap().put("expire",defaultExpire);
        map.getMap().put("timeUnit",defaultTimeUnit);
        return map;
    }

    /**
     * redisTemplate 序列化使用的jdkSerializeable, 存储二进制字节码, 所以自定义序列化类
     * @param redisConnectionFactory
     * @return
     */
    @Bean(name = "cacheRedisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        MsgPackSerializer msgSerializer = new MsgPackSerializer();

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(msgSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }


}
