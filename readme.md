@EnableMethodCache 标注在配置类上,开启缓存
  region:缓存key前缀
  timeUnit:时间单位
  expire:超时时间,配合单位,设置全局缓存时间
  
@CacheAdd 将方法返回结果存储缓存,标注此注解的方法,每次都会被调用,方法新的返回新值会覆盖旧址
  region:同@EnableMethodCache region,不填则默认使用@EnableMethodCache 中配置的
  key: 缓存key,配合region生成最终的key
  timeUnit:超时时间,如果在此注解上配置,则覆盖@EnableMethodCache的时间单位
  expire:过期时间,如果在此注解上配置,则覆盖@EnableMethodCache的过期时间
  cacheNullValue:是否存储null,默认不存储
  
@Cached 将方法返回结果存储缓存,标注此注解的方法,第一次调用将执行,如果再次调用将查询缓存,命中则直接返回结果
  region:同@EnableMethodCache region,不填则默认使用@EnableMethodCache 中配置的
  key: 缓存key,配合region生成最终的key
  timeUnit:超时时间,如果在此注解上配置,则覆盖@EnableMethodCache的时间单位
  expire:过期时间,如果在此注解上配置,则覆盖@EnableMethodCache的过期时间
  cacheNullValue:是否存储null,默认不存储
 
@CacheRemove 从缓存移除
  region:同@EnableMethodCache region,不填则默认使用@EnableMethodCache 中配置的
  key:缓存key,配合region生成最终的key
  allKey:配置此值则将删除以 region开头的所有key

**使用** 
1.配置文件中需要配置Redis相关信息
```$xslt
  redis:
    host: 127.0.0.1
    port: 6379
    jedis:
      pool:
        max-active: 20
        min-idle: 1
```
2.开启缓存
```$xslt
    @SpringBootApplication
    @MapperScan("com.cainiaolc.config.server.mapper")
    @EnableMethodCache(region = "config-admin",expire =30,timeUnit = TimeUnit.MINUTES)
    public class ConfigServerApplication {
    
        public static void main(String[] args) {
            SpringApplication.run(ConfigServerApplication.class, args);
        }
    }
```
3.使用注解
```$xslt
    @Cached(key="'pubItem:selectItemByNSIdAndKey:'.concat(#namespaceId).concat(:).concat(#key)")
    public List<TPubItem> selectItemByNSIdAndKey(Integer namespaceId, String key) {
        TPubItemExample example = new TPubItemExample();
        example.createCriteria().andKeyEqualTo(key).andPubNamespaceIdEqualTo(namespaceId);
        List<TPubItem> pubItems = pubItemMapper.selectByExample(example);
        return pubItems;
    }
```