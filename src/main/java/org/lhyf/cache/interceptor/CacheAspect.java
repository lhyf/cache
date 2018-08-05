package org.lhyf.cache.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.lhyf.cache.annotation.CacheEvict;
import org.lhyf.cache.annotation.CachePut;
import org.lhyf.cache.annotation.Cached;
import org.lhyf.cache.config.ConfigMap;
import org.lhyf.cache.exception.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/****
 * @author YF
 * @date 2018-08-04 16:52
 * @desc CacheAspect
 *
 **/
@Component
@Aspect
public class CacheAspect {

    @Resource(name = "cacheRedisTemplate")
    private RedisTemplate<Object, Object> template;

    @Autowired
    private ConfigMap configMap;

    private LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
    private ExpressionParser parser = new SpelExpressionParser();


    /**
     * 解析 @Cached 注解
     * 先从缓存获取,若缓存有则返回,没有则调用目标方法,再将方法返回的值存储缓存
     *
     * @param joinPoint
     * @return
     */
    @Around("@annotation(org.lhyf.cache.annotation.Cached)")
    private Object cachedProcess(ProceedingJoinPoint joinPoint) {
        Object result = null;

        try {
            String region = (String) configMap.getMap().get("region");
            int expire = (int) configMap.getMap().get("expire");
            TimeUnit timeUnit = (TimeUnit) configMap.getMap().get("timeUnit");

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Object[] args = joinPoint.getArgs();
            Cached cached = method.getAnnotation(Cached.class);
            String seplkey = cached.key();

            // 如果单独配置了,则覆盖全局配置
            if (StringUtils.isNotEmpty(cached.region())) {
                region = cached.region();
            }

            if (cached.expire() != 0) {
                expire = cached.expire();
            }

            if(cached.timeUnit() != TimeUnit.MILLISECONDS){
                timeUnit = cached.timeUnit();
            }

            boolean b = cached.cacheNullValue();
            EvaluationContext spelContext = getSpelContext(method, args);
            String key = getKey(spelContext, seplkey);

            // 从缓存中获取
            result = cacheGet(region + ":" + key);

            if (result != null) {
                return result;
            }

            result = joinPoint.proceed(args);

            // 是否存储空值
            if (result != null || b) {
                cachePut(region + ":" + key, result, expire, timeUnit);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            new CacheException(throwable);
        }

        return result;
    }


    /**
     * 解析 @CachePut 注解
     * 无论缓存是否存在值,都将执行目标方法
     * 并将返回的值存储缓存
     *
     * @param joinPoint
     * @param result
     */
    @AfterReturning(value = "@annotation(org.lhyf.cache.annotation.CachePut)", returning = "result")
    private void cachePutProcess(JoinPoint joinPoint, Object result) {
        try {

            String region = (String) configMap.getMap().get("region");
            int expire = (int) configMap.getMap().get("expire");
            TimeUnit timeUnit = (TimeUnit) configMap.getMap().get("timeUnit");

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Object[] args = joinPoint.getArgs();
            CachePut cachePut = method.getAnnotation(CachePut.class);
            String seplkey = cachePut.key();

            // 如果单独配置了,则覆盖全局配置
            if (StringUtils.isNotEmpty(cachePut.region())) {
                region = cachePut.region();
            }

            if (cachePut.expire() != 0) {
                expire = cachePut.expire();
            }

            if(cachePut.timeUnit() != TimeUnit.MILLISECONDS){
                timeUnit = cachePut.timeUnit();
            }
            boolean b = cachePut.cacheNullValue();
            EvaluationContext spelContext = getSpelContext(method, args);
            String key = getKey(spelContext, seplkey);

            // 是否存储空值
            if (result != null || b) {
                cachePut(region + ":" + key, result, expire, timeUnit);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CacheException(e);
        }
    }


    /**
     * 解析 @CacheEvict 注解
     * allKey:true 删除所有region开头的key
     * 否则 根据具体的key 删除缓存
     *
     * @param joinPoint
     * @return
     */
    @Around("@annotation(org.lhyf.cache.annotation.CacheEvict)")
    private Object cachedCacheEvict(ProceedingJoinPoint joinPoint) {
        Object result = null;

        try {
            String region = (String) configMap.getMap().get("region");
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Object[] args = joinPoint.getArgs();
            CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);

            String[] seplkey = cacheEvict.key();
            // 如果单独配置了,则覆盖全局配置
            if (StringUtils.isNotEmpty(cacheEvict.region())) {
                region = cacheEvict.region();
            }
            boolean allKey = cacheEvict.allKey();

            //删除所有region开头的Key
            if (allKey) {
                Set<Object> keys = template.keys(region + ":*");
                template.delete(keys);
            }

            if (!allKey) {
                EvaluationContext spelContext = getSpelContext(method, args);

                for (String k : seplkey) {
                    String key = getKey(spelContext, k);
                    Set<Object> keys = template.keys(region + ":" + key);
                    template.delete(keys);
                }

            }
            result = joinPoint.proceed(args);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            new CacheException(throwable);
        }

        return result;
    }


    /**
     * 存入缓存
     *
     * @param key      键
     * @param result   值
     * @param expire   过期时间
     * @param timeUnit 时间单位
     */
    private void cachePut(String key, Object result, int expire, TimeUnit timeUnit) {
        template.opsForValue().set(key, result);
        template.expire(key, expire, timeUnit);
    }


    /**
     * 从缓存获取
     *
     * @param key
     * @return
     */
    private Object cacheGet(String key) {
        return template.opsForValue().get(key);
    }

    /**
     * 生成SPEL 上下文环境
     *
     * @param method
     * @param args
     * @return
     */
    private EvaluationContext getSpelContext(Method method, Object[] args) {
        String[] params = discoverer.getParameterNames(method);
        EvaluationContext context = new StandardEvaluationContext();
        for (int len = 0; len < params.length; len++) {
            context.setVariable(params[len], args[len]);
        }
        return context;
    }

    /**
     * 解析SPEL
     *
     * @param context
     * @param key
     * @return
     */
    private String getKey(EvaluationContext context, String key) {
        Expression expression = parser.parseExpression(key);
        String value = expression.getValue(context, String.class);
        return value;
    }
}
