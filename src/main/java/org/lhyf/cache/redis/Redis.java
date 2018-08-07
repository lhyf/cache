package org.lhyf.cache.redis;


import org.apache.commons.lang3.StringUtils;
import org.lhyf.cache.serializer.MsgPackUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;

/**
 * Redis缓存的相关方法
 * 
 * @author niujf
 * @created 2014年2月24日 下午9:50:18
 */
public class Redis extends Remote{

	private static final Logger logger = LoggerFactory.getLogger(Redis.class);


	private Map<String, String> props = new HashMap<String, String>();
	private static JedisPool pool;

	/**
	 * 初始化redis客户端
	 * 
	 * @param props
	 */
	public void init(Map<String, String> props) {
		JedisPoolConfig config = new JedisPoolConfig();

        String host = props.get("host");
        String port = props.get("port");
        String passwd = props.get("password");
        String poolsize = props.get("poolsize");
        String maxConn = props.get("maxConn");
        String timeout = props.get("timeout");

		config.setWhenExhaustedAction((byte)1);
		config.setMaxIdle(Integer.parseInt(poolsize));
		config.setMinIdle(Integer.parseInt(poolsize));
		config.setMaxActive(Integer.parseInt(maxConn));
		config.setMaxWait(100);
		config.setTestWhileIdle(false);
		config.setTestOnBorrow(false);
		config.setTestOnReturn(false);
		config.setNumTestsPerEvictionRun(10);
		config.setMinEvictableIdleTimeMillis(1000);
		config.setSoftMinEvictableIdleTimeMillis(10);
		config.setTimeBetweenEvictionRunsMillis(10);
		config.lifo = false;
        try{
        	if(null != pool){
        		pool.destroy();
        	}
            pool = new JedisPool(config, host, Integer.parseInt(port), Integer.parseInt(timeout), passwd);
        }catch(RuntimeException e){
			logger.error("Init jredis pool error!", e);
        }
		this.props = props;
	}
	
	private Jedis get(){
        try{
            return pool.getResource();
        }catch(RuntimeException e){
			logger.error("", e);
        }
        return null;
	}

    public List<String> keys(String pattern){
        List<String> result = new ArrayList<String>();
        boolean broken = false;
        Jedis client = get();
        if(null == client){
            return result;
        }
        try{
            Set<String> list = client.keys(pattern);
            if(null != list){
                result.addAll(list);
            }
        }catch(Exception e){
            broken = true;
			logger.error("Redis get keys error!", e);
        }finally{
            closeClient(client, broken);
        }
        return result;
    }

	/**
	 * 重新加载客户端
	 */
	public void reloadClient() {
		init(props);
	}

	public String spop(String key){
		boolean broken = false;
		Jedis client = get();
		if(null == client){
			return null;
		}
		try{
			return new String(client.spop(key.getBytes()));
		}catch(Exception e){
			logger.error("", e);
			broken = true;
		}finally{
			closeClient(client, broken);
		}
		return null;
	}

	/**
	 * 新增加一个获取方法
	 * @param key
	 * @return
	 */
	public String srandmember(String key){
		boolean broken = false;
		Jedis client = get();
		if(null == client){
			return null;
		}
		try{
			return new String(client.srandmember(key.getBytes()));
		}catch(Exception e){
			logger.error("", e);
			broken = true;
		}finally{
			closeClient(client, broken);
		}
		return null;
	}
	public void sadd(String key, Object value){
		boolean broken = false;
		Jedis client = get();
		if(null == client){
			return ;
		}
		try{
			client.sadd(key.getBytes(), String.valueOf(value).getBytes());
		}catch(Exception e){
			logger.error("", e);
			broken = true;
		}finally{
			closeClient(client, broken);
		}
	}

	public String hget(String key, String field){
		boolean broken = false;
		Jedis client = get();
		if(null == client){
			return null;
		}
		try{
			String datas = client.hget(key, field);
			if(StringUtils.isBlank(datas)){
				return null;
			}
			return datas;
		}catch(Exception e){
			logger.error("", e);
			broken = true;
		}finally{
			closeClient(client, broken);
		}
		return null;
	}

	/**
	 * 向远程缓存添加数据
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value, int timeout) {
		boolean broken = false;
		Jedis client = get();
        if(null == client){
            return ;
        }
		try{
        	byte[] datas = serialize(value);
        	if(null == datas){
        		return ;
			}
			client.set(key.getBytes(), serialize(value));
			if(timeout != 0){
	            client.expire(key.getBytes(), timeout);
			}else{
				//此处用于防止缓存内存占用过大，如果预估不大的情况下，可删除
//				client.expire(key.getBytes(), 604800);
			}
		}catch(Exception e){
			logger.error("", e);
			broken = true;
		}finally{
			closeClient(client, broken);
		}
	}

    public void expire(String key, int timeout){
        if(0 == timeout){
            return ;
        }
        boolean broken = false;
        Jedis client = get();
        if(null == client){
            return ;
        }
        try{
            client.expire(key.getBytes(), timeout);
        }catch(Exception e){
            broken = true;
			logger.error("Redis expire error!", e);
        }finally{
            closeClient(client, broken);
        }
    }

	/**
	 * 删除远程缓存数据
	 * @param key
	 */
	public void remove(String key) {
		boolean broken = false;
		Jedis client = get();
        if(null == client){
            return ;
        }
		try{
			client.del(key.getBytes());
		}catch(Exception e){
			broken = true;
			logger.error("Redis remove error!", e);
		}finally{
			closeClient(client, broken);
		}
	}

    @Override
    public void remove(List<String> keys) {
        if(null == keys || keys.size() == 0){
            return ;
        }
        boolean broken = false;
        Jedis client = get();
        if(null == client){
            return ;
        }
        try{
            String[] array = new String[keys.size()];
            client.del(keys.toArray(array));
        }catch(Exception e){
            broken = true;
			logger.error("Redis remove error!", e);
        }finally{
            closeClient(client, broken);
        }
    }

    /**
	 * 从远程缓存获取数据
	 * @param key
	 * @return
	 */
	public <T> T get(String key, Class<T> clazz) {
		boolean broken = false;
		Jedis client = get();
        if(null == client){
            return null;
        }
		try{
			return (T)unserialize(client.get(key.getBytes()), clazz);
		}catch(Exception e){
			broken = true;
			logger.error("Redis get error!", e);
		}finally{
			closeClient(client, broken);
		}
		return null;
	}

	/**
	 * 放到队首<br/>
	 * 用于消息队列，所有key之前都会新加一个queue_关键词
	 * @param key
	 * @param value
	 */
	protected void lpush(String key, byte[] value){
		if(key == null || key.trim().equals("")){
			return ;
		}
		if(null == value){
			value = null;
		}
		boolean broken = false;
		Jedis client = get();
		if(null == client){
			return ;
		}
		try{
			key = "queue_" + key;
			client.lpush(key.getBytes(), value);
		}catch(Exception e){
			logger.error("", e);
			broken = true;
		}finally{
			closeClient(client, broken);
		}
	}

	/**
	 * 放到队尾<br/>
	 * 用于消息队列，所有key之前都会新加一个queue_关键词
	 * @param key
	 * @param value
	 */
	protected void rpush(String key, byte[] value){
		if(key == null || key.trim().equals("")){
			return ;
		}
		if(null == value){
			value = null;
		}
		boolean broken = false;
		Jedis client = get();
		if(null == client){
			return ;
		}
		try{
			key = "queue_" + key;
			client.rpush(key.getBytes(), value);
		}catch(Exception e){
			logger.error("", e);
			broken = true;
		}finally{
			closeClient(client, broken);
		}
	}

	/**
	 * 获取队列数据，读队首
	 * @param key
	 * @return
	 */
	protected byte[] lpop(String key){
		if(key == null || key.trim().equals("")){
			return null;
		}
		boolean broken = false;
		Jedis client = get();
		if(null == client){
			return null;
		}
		try{
			key = "queue_" + key;
			return client.lpop(key.getBytes());
		}catch(Exception e){
			broken = true;
			logger.error("Redis get error!", e);
		}finally{
			closeClient(client, broken);
		}
		return null;
	}

	/**
	 * 读队尾<br/>
	 * 获取队列数据
	 * @param key
	 * @return
	 */
	protected byte[] rpop(String key){
		if(key == null || key.trim().equals("")){
			return null;
		}
		boolean broken = false;
		Jedis client = get();
		if(null == client){
			return null;
		}
		try{
			key = "queue_" + key;
			return client.lpop(key.getBytes());
		}catch(Exception e){
			broken = true;
			logger.error("Redis get error!", e);
		}finally{
			closeClient(client, broken);
		}
		return null;
	}

	/**
	 * 将对象序列化
	 * @param value
	 * @return
	 */
	private byte[] serialize(Object value) {
		if(null == value){
			return null;
		}
		return MsgPackUtil.toBytes(value);
	}

	/**
	 * 将字节反序列化
	 * @param bytes
	 * @return
	 */
	private <T> T unserialize(byte[] bytes, Class<T> clazz) {
		if(null == bytes){
			return null;
		}
		return (T)MsgPackUtil.toObject(bytes,clazz);
	}

	@Override
	public Map<String, Object> get(List<String> keys) {
		return null;
	}

	@Override
	public void shutdown() {
		if(null != pool){
			try{
				pool.destroy();
			}catch(RuntimeException e){
				logger.error("", e);
			}
		}
	}
	
	private void closeClient(Jedis client, boolean broken){
		if(null == client){
			return ;
		}
		if(broken){
			pool.returnBrokenResource(client);
			client = null;
		}else{
			pool.returnResource(client);
		}
	}

	@Override
	public void clear() {
		boolean broken = false;
		Jedis client = get();
        if(null == client){
            return ;
        }
		try{
			client.flushDB();
		}catch(RuntimeException e){
			broken = true;
			logger.error("Redis get error!", e);
		}finally{
			closeClient(client, broken);
		}
	}

	@Override
	public void clear(String region) {
		boolean broken = false;
		Jedis client = get();
        if(null == client){
            return ;
        }
		try{
			Set<String> keys = client.keys(region+"_*");
			logger.debug("remove remote cache: " + region);
			if(null != keys && keys.size() > 0){
				for(String key : keys){
					logger.debug("remove remote cache: " + key);
					client.del(key.getBytes());
				}
			}
		}catch(RuntimeException e){
			broken = true;
			logger.error("Redis get error!", e);
		}finally{
			closeClient(client, broken);
		}
	}

}
