package org.lhyf.cache.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lhyf.cache.annotation.EnableMethodCache;
import org.lhyf.cache.redis.Redis;
import org.lhyf.cache.redis.Remote;
import org.lhyf.cache.serializer.MsgSerializer;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;
import java.util.Map;
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

        // 使用Jackson2JsonRedisSerialize 替换默认序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        MsgSerializer msgSerializer = new MsgSerializer();

//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//
//        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 设置value的序列化规则和 key的序列化规则
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(msgSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }




//    @Bean
//    public CacheManager cacheManager(RedisConnectionFactory factory){
//        RedisCacheManager cacheManager = RedisCacheManager.create(factory);
//        Cache cache = cacheManager.getCache("11");
//        cache.put("AAA","aa");
//        System.out.println(cache.get("AAA").get());
//        return cacheManager;
//    }

    /**
     *
     @Value("#{host}")
     private String host;

     @Value("#{port}")
     private String port;

     @Value("#{passwd}")
     private String passwd;

     @Value("#{poolsize}")
     private String poolsize;

     @Value("#{maxConn}")
     private String maxConn;

     @Value("#{timeout}")
     private String timeout;
     * @param properties
     * @return
     */
//    @Bean
//    public Remote remote(CacheRedisProperties properties){
//        Map<String, String> props = new HashMap<>();
//        props.put("host",properties.getHost());
//        props.put("port",properties.getPort());
//        props.put("passwd",properties.getPasswd());
//        props.put("poolsize",properties.getPoolsize());
//        props.put("maxConn",properties.getMaxConn());
//        props.put("timeout",properties.getTimeout());
//
//        Redis redis = new Redis();
//        redis.init(props);
//        return redis;
//    }

}
