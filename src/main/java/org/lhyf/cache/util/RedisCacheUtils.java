package org.lhyf.cache.util;


import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/****
 * @author YF
 * @date 2018-08-07 21:24
 * @desc RedisCacheUtils
 *
 **/
@Component
public class RedisCacheUtils {
    private static Logger logger = LoggerFactory.getLogger(RedisCacheUtils.class);

    @Resource(name = "cacheRedisTemplate")
    private RedisTemplate redisTemplate;

    @Resource
    RedisConnectionFactory redisConnectionFactory;



    /**
     * 查询Key
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern){
        logger.debug("查询Keys, key:" + pattern);
        try {
            Set keys = redisTemplate.keys(pattern);
            return keys;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new HashSet<>();
        }
    }

    /**
     * 批量删除Key
     * @param keys
     */
    public void delete(Set<String> keys){
        logger.debug("批量删除Keys, key:" + keys);
        try {
            redisTemplate.delete(keys);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     * @return缓存的对象
     */
    public boolean setCacheObject(String key, Object value, int expire, TimeUnit timeUnit) {
        logger.debug("存入缓存 key:{}, 缓存时间为:{} 单位: {} ",key,expire,timeUnit.toString());
        try {
            ValueOperations<String, Object> operation = redisTemplate.opsForValue();
            operation.set(key,value,expire,timeUnit);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return false;
        }
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     * @return缓存的对象
     */
    public boolean setCacheObject(String key, Object value) {
        logger.debug("存入缓存 key:" + key);
        try {
            ValueOperations<String, Object> operation = redisTemplate.opsForValue();
            operation.set(key, value);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return false;
        }
    }

    /**
     * 根据pattern匹配清除缓存
     *
     * @param pattern
     */
    public void clear(String pattern) {
        logger.debug("清除缓存 pattern:" + pattern);
        try {
            ValueOperations<String, Object> valueOper = redisTemplate.opsForValue();
            RedisOperations<String, Object> redisOperations = valueOper.getOperations();
            redisOperations.keys(pattern);
            Set<String> keys = redisOperations.keys(pattern);
            for (String key : keys) {
                redisOperations.delete(key);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return;
        }
    }

    /**
     * 根据key清除缓存
     *
     * @param key
     */
    public void delete(String key) {
        logger.debug("删除缓存 key:" + key);
        try {
            ValueOperations<String, Object> valueOper = redisTemplate.opsForValue();
            RedisOperations<String, Object> redisOperations = valueOper.getOperations();
            redisOperations.delete(key);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return;
        }
    }


    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     *
     */
    public Object getCacheObject(String key) {

        logger.debug("获取缓存 key:" + key);
        try {
            ValueOperations<String, Object> operation = redisTemplate.opsForValue();
            return operation.get(key);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     *
     */
    public <T> T getCacheObject(String key, Class<T> clazz) {

        logger.debug("获取缓存 key:" + key);
        RedisTemplate template = new StringRedisTemplate(redisConnectionFactory);
        Jackson2JsonRedisSerializer Jackson2Serializer = new Jackson2JsonRedisSerializer(clazz);
        Jackson2Serializer.setObjectMapper(new ObjectMapper(new MessagePackFactory()));
        RedisSerializer redisSerializer = Jackson2Serializer;
        template.setValueSerializer(redisSerializer);
        try {
            ValueOperations<String, T> operation = template.opsForValue();
            return (T) operation.get(key);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

}