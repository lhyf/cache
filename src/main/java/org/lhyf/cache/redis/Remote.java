package org.lhyf.cache.redis;



import java.util.List;
import java.util.Map;

public abstract class Remote {
	
	public abstract void put(String key, Object value, int timeout);

	public abstract String hget(String key, String field);

	public abstract String spop(String key);

	public abstract void sadd(String key, Object value);

    public abstract void expire(String key, int timeout);
	
	public abstract void remove(String key);

    public abstract void remove(List<String> keys);
	
	public abstract <T> T get(String key, Class<T> clazz);

	protected abstract void lpush(String key, byte[] value);

	protected abstract void rpush(String key, byte[] value);

	protected abstract byte[] lpop(String key);

	protected abstract byte[] rpop(String key);
	
	public abstract List<String> keys(String pattern);
	
	public abstract void init(Map<String, String> props);
	
	public abstract Map<String, Object> get(List<String> keys);
	
	public abstract void clear();
	
	public abstract void clear(String region);
	
	public abstract void reloadClient();
	
	public abstract void shutdown();
	
}
