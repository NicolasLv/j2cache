/**
 * Copyright (c) 2015-2017, Winter Lau (javayou@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.j2cache;

import net.oschina.j2cache.caffeine.CaffeineProvider;
import net.oschina.j2cache.ehcache.EhCacheProvider3;
import net.oschina.j2cache.redis.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.oschina.j2cache.ehcache.EhCacheProvider;
import net.oschina.j2cache.redis.RedisCacheProvider;

import java.util.Properties;

/**
 * 两级的缓存管理器
 * @author Winter Lau(javayou@gmail.com)
 */
public class CacheProviderHolder {

	private final static Logger log = LoggerFactory.getLogger(CacheProviderHolder.class);

	private static CacheProvider l1_provider;
	private static CacheProvider l2_provider;

	private static CacheExpiredListener listener;

	/**
	 * Initialize Cache Provider
	 * @param listener cache listener
	 */
	public static void init(Properties props, CacheExpiredListener listener){
		CacheProviderHolder.listener = listener;
		try {
			CacheProviderHolder.l1_provider = loadProviderInstance(props.getProperty("j2cache.L1.provider_class"));
			if (!l1_provider.isLevel(CacheObject.LEVEL_1))
				throw new CacheException(l1_provider.getClass().getName() + " is not level_1 cache provider");
			CacheProviderHolder.l1_provider.start(loadProviderProperties(props, CacheProviderHolder.l1_provider));
			log.info("Using L1 CacheProvider : " + l1_provider.getClass().getName());

			CacheProviderHolder.l2_provider = loadProviderInstance(props.getProperty("j2cache.L2.provider_class"));
			if (!l2_provider.isLevel(CacheObject.LEVEL_2))
				throw new CacheException(l2_provider.getClass().getName() + " is not level_2 cache provider");
			CacheProviderHolder.l2_provider.start(loadProviderProperties(props, CacheProviderHolder.l2_provider));
			log.info("Using L2 CacheProvider : " + l2_provider.getClass().getName());
		} catch (CacheException e) {
			throw e;
		}
	}

	/**
	 * 关闭缓存
	 */
	public final static void shutdown() {
		l1_provider.stop();
		l2_provider.stop();
	}

	public static RedisClient getRedisClient() {
		return ((RedisCacheProvider)l2_provider).getClient();
	}

	private final static CacheProvider loadProviderInstance(String cacheIdent) {
		if("ehcache".equalsIgnoreCase(cacheIdent))
			return new EhCacheProvider();
		if("ehcache3".equalsIgnoreCase(cacheIdent))
			return new EhCacheProvider3();
		if("caffeine".equalsIgnoreCase(cacheIdent))
			return new CaffeineProvider();
		if("redis".equalsIgnoreCase(cacheIdent))
			return new RedisCacheProvider();
		if("none".equalsIgnoreCase(cacheIdent))
			return new NullCacheProvider();
		try {
			return (CacheProvider) Class.forName(cacheIdent).newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new CacheException("Failed to initialize cache providers", e);
		}
	}
	
	private final static Properties loadProviderProperties(Properties props, CacheProvider provider) {
		String prefix = provider.name() + '.';
		Properties new_props = new Properties();
		for(String key : props.stringPropertyNames()) {
			if(key.startsWith(prefix))
				new_props.setProperty(key.substring(prefix.length()), props.getProperty(key));
		}
		return new_props;
	}

	/**
	 * 一级缓存实例
	 * @param region
	 * @return
	 */
	public final static Level1Cache getLevel1Cache(String region) {
		return (Level1Cache)l1_provider.buildCache(region, listener);
	}

	/**
	 * 一级缓存实例
	 * @param region
	 * @param timeToLiveSeconds
	 * @return
	 */
	public final static Level1Cache getLevel1Cache(String region, long timeToLiveSeconds) {
		return (Level1Cache)l1_provider.buildCache(region, timeToLiveSeconds, listener);
	}

	/**
	 * 二级缓存实例
	 * @param region
	 * @return
	 */
	public final static Level2Cache getLevel2Cache(String region) {
		return (Level2Cache)l2_provider.buildCache(region, listener);
	}

}
