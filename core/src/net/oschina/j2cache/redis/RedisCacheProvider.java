/**
 * Copyright (c) 2015-2017, Winter Lau (javayou@gmail.com), wendal.
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
package net.oschina.j2cache.redis;

import net.oschina.j2cache.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Redis 缓存管理，实现对多种 Redis 运行模式的支持和自动适配，实现连接池管理等
 *
 * @author Winter Lau (javayou@gmail.com)
 * @author wendal
 */
public class RedisCacheProvider implements CacheProvider {

    private final static Logger log = LoggerFactory.getLogger(RedisCacheProvider.class);

    private RedisClient redisClient;
    private String namespace;
    private String storage;
    protected ConcurrentHashMap<String, Cache> caches = new ConcurrentHashMap<>();

    @Override
    public String name() {
        return "redis";
    }

    @Override
    public int level() {
        return CacheObject.LEVEL_2;
    }

    public RedisClient getClient() {
        return redisClient;
    }

    /**
     * 初始化 Redis 连接
     * @param props current configuration settings.
     */
    @Override
    public void start(Properties props) {
        this.namespace = props.getProperty("namespace");
        this.storage = props.getProperty("storage");

        JedisPoolConfig poolConfig = newPoolConfig(props);

        String hosts = props.getProperty("hosts");
        String mode = props.getProperty("mode");
        String cluster_name = props.getProperty("cluster_name");
        String password = props.getProperty("password");
        int database = Integer.parseInt(props.getProperty("database"));
        this.redisClient = new RedisClient.Builder()
                .mode(mode)
                .hosts(hosts)
                .password(password)
                .cluster(cluster_name)
                .database(database)
                .poolConfig(poolConfig).newClient();

        log.info(String.format("Redis client starts with mode(%s), db(%d), storage(%s), namespace(%s)", mode, database, storage, namespace));
    }

    @Override
    public void stop() {
        caches.clear();
        try {
            redisClient.close();
        } catch (IOException e) {
            log.warn("Failed to close redis connection.", e);
        }
    }

    @Override
    public Cache buildCache(String region, CacheExpiredListener listener) {
        Cache cache = caches.get(region);
        if (cache == null) {
            synchronized(RedisCacheProvider.class) {
                if(cache == null) {
                    if("hash".equalsIgnoreCase(this.storage))
                        cache = new RedisHashCache(this.namespace, region, redisClient);
                    else
                        cache = new RedisGenericCache(this.namespace, region, redisClient);
                    caches.put(region, cache);
                }
            }
        }
        return cache;
    }

    @Override
    public Cache buildCache(String region, long timeToLiveInSeconds, CacheExpiredListener listener) {
        return buildCache(region, listener);
    }

    /**
     * 初始化 Redis 连接池
     * @param props
     * @return
     */
    private JedisPoolConfig newPoolConfig(Properties props) {
        JedisPoolConfig cfg = new JedisPoolConfig();
        cfg.setMaxTotal(Integer.valueOf((String)props.getOrDefault("maxTotal", "-1")));
        cfg.setMaxIdle(Integer.valueOf((String)props.getOrDefault("maxIdle", "100")));
        cfg.setMaxWaitMillis(Integer.valueOf((String)props.getOrDefault("maxWaitMillis", 100)));
        cfg.setMinEvictableIdleTimeMillis(Integer.valueOf((String)props.getOrDefault("minEvictableIdleTimeMillis", "864000000")));
        cfg.setMinIdle(Integer.valueOf((String)props.getOrDefault("minIdle", "10")));
        cfg.setNumTestsPerEvictionRun(Integer.valueOf((String)props.getOrDefault("numTestsPerEvictionRun", "10")));
        cfg.setLifo(Boolean.valueOf(props.getProperty("lifo", "false")));
        cfg.setSoftMinEvictableIdleTimeMillis(Integer.valueOf((String)props.getOrDefault("softMinEvictableIdleTimeMillis", "10")));
        cfg.setTestOnBorrow(Boolean.valueOf(props.getProperty("testOnBorrow", "true")));
        cfg.setTestOnReturn(Boolean.valueOf(props.getProperty("testOnReturn", "false")));
        cfg.setTestWhileIdle(Boolean.valueOf(props.getProperty("testWhileIdle", "false")));
        cfg.setTimeBetweenEvictionRunsMillis(Integer.valueOf((String)props.getOrDefault("timeBetweenEvictionRunsMillis", "300000")));
        cfg.setBlockWhenExhausted(Boolean.valueOf(props.getProperty("blockWhenExhausted", "true")));
        return cfg;
    }

}
