package com.edium.service.data.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.Callable;

class CustomRedisCacheManager extends RedisCacheManager {
    private static Logger logger = LoggerFactory.getLogger(CustomRedisCacheManager.class);

    public CustomRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    public CustomRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, String... initialCacheNames) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheNames);
    }

    public CustomRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, boolean allowInFlightCacheCreation, String... initialCacheNames) {
        super(cacheWriter, defaultCacheConfiguration, allowInFlightCacheCreation, initialCacheNames);
    }

    public CustomRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, Map<String, RedisCacheConfiguration> initialCacheConfigurations) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations);
    }

    public CustomRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, Map<String, RedisCacheConfiguration> initialCacheConfigurations, boolean allowInFlightCacheCreation) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations, allowInFlightCacheCreation);
    }

    @Override
    public Cache getCache(String name) {
        return new RedisCacheWrapper(super.getCache(name));
    }

    protected static class RedisCacheWrapper implements Cache {

        private final Cache delegate;
        private String cacheName = "";

        public RedisCacheWrapper(Cache redisCache) {
            Assert.notNull(redisCache, "delegate cache must not be null");
            this.delegate = redisCache;
        }

        @Override
        public String getName() {
            try {
                cacheName = delegate.getName();
                return cacheName;
            } catch (Exception e) {
                return handleException(e, "get cache name");
            }
        }

        @Override
        public Object getNativeCache() {
            try {
                return delegate.getNativeCache();
            } catch (Exception e) {
                return handleException(e, "get native cache");
            }
        }

        @Override
        public Cache.ValueWrapper get(Object key) {
            try {
                return delegate.get(key);
            } catch (Exception e) {
                return handleException(e, cacheName, key, "get");
            }
        }

        @Override
        public <T> T get(Object o, Class<T> aClass) {
            try {
                return delegate.get(o, aClass);
            } catch (Exception e) {
                return handleException(e, cacheName, o, "get");
            }
        }

        @Override
        public <T> T get(Object o, Callable<T> callable) {
            try {
                return delegate.get(o, callable);
            } catch (Exception e) {
                return handleException(e, cacheName, o, "get");
            }
        }

        @Override
        public void put(Object key, Object value) {
            try {
                delegate.put(key, value);
            } catch (Exception e) {
                handleException(e, cacheName, key, value, "put");
            }
        }

        @Override
        public ValueWrapper putIfAbsent(Object o, Object o1) {
            try {
                return delegate.putIfAbsent(o, o1);
            } catch (Exception e) {
                return handleException(e, cacheName, o, o1, "putIfAbsent");
            }
        }

        @Override
        public void evict(Object o) {
            try {
                delegate.evict(o);
            } catch (Exception e) {
                handleException(e, cacheName, o, "evict");
            }
        }

        @Override
        public void clear() {
            try {
                delegate.clear();
            } catch (Exception e) {
                handleException(e, cacheName, "clear");
            }
        }

        private <T> T handleException(Exception e, String action) {
            logger.error("handleException", e.getMessage());
            logger.warn("Unable to " + action + " : " + e.getMessage());
            return null;
        }

        private <T> T handleException(Exception e, String cacheName, String action) {
            logger.error("handleException", e.getMessage());
            logger.warn("Unable to " + action + " from cache " + cacheName + " : " + e.getMessage());
            return null;
        }

        private <T> T handleException(Exception e, String cacheName, Object key, String action) {
            logger.error("handleException", e.getMessage());
            logger.warn("Unable to " + action + " for key " + key + " from cache " + cacheName + " : " + e.getMessage());
            return null;
        }

        private <T> T handleException(Exception e, String cacheName, Object key, Object value, String action) {
            logger.error("handleException", e.getMessage());
            logger.warn("Unable to " + action + " for key " + key + " and value " + value + " from cache " + cacheName + " : " + e.getMessage());
            return null;
        }
    }
}
