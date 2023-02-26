package org.changmoxi.vhr.common.cache;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.changmoxi.vhr.common.info.DoubleCacheConfigInfo;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 自定义整合 Caffeine的Cache 和 RedisTemplate 的缓存组件，在缓存组件的构造器中传入 Caffeine的Cache 和 RedisTemplate
 * Spring的Cache接口{@link org.springframework.cache.Cache} 规范了缓存组件的定义，包含了缓存的各种操作，实现具体缓存操作的管理。
 * 在Cache接口中，定义了get、put、evict、clear等方法，分别对应缓存的取出、写入、删除、清空等操作。
 * 我们要自定义缓存组件，除了直接实现Cache接口，还可以继承 抽象类{@link AbstractValueAdaptingCache}，它已经实现了Cache接口，并且在Cache接口基础上进行了一层封装
 *
 * @author CZS
 * @create 2023-02-24 15:34
 **/
@Slf4j
public class DoubleCache extends AbstractValueAdaptingCache {
    private String cacheName;
    private DoubleCacheConfigInfo doubleCacheConfigInfo;
    private Cache<Object, Object> caffeineCache;
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Create an {@code AbstractValueAdaptingCache} with the given setting.
     *
     * @param allowNullValues whether to allow for {@code null} values
     */
    protected DoubleCache(boolean allowNullValues) {
        super(allowNullValues);
    }

    /**
     * 在 {@link DoubleCacheManager} 中调用这个构造器来传入 Caffeine的Cache 和 RedisTemplate，实例化DoubleCache缓存组件
     *
     * @param cacheName
     * @param doubleCacheConfigInfo
     * @param caffeineCache
     * @param redisTemplate
     */
    public DoubleCache(String cacheName, DoubleCacheConfigInfo doubleCacheConfigInfo, Cache<Object, Object> caffeineCache, RedisTemplate<String, Object> redisTemplate) {
        super(doubleCacheConfigInfo.getAllowNullValues());
        this.cacheName = cacheName;
        this.doubleCacheConfigInfo = doubleCacheConfigInfo;
        this.caffeineCache = caffeineCache;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 在缓存中实际执行的查找操作
     * 比如当一个方法添加了@Cacheable注解后，操作缓存时会先调用父类的 {@link AbstractValueAdaptingCache#get(Object)} 方法
     * 然后再去调用这里实现的 {@link DoubleCache#lookup(Object)} 方法
     *
     * @param key the key whose associated value is to be returned
     * @return 如果返回值不为null，则会直接返回给调用方，不执行原方法。如果返回值为null，就会执行原方法，执行完之后调用 {@link DoubleCache#put(Object, Object)} 方法写入缓存
     */
    @Override
    protected Object lookup(Object key) {
        // 先查询Caffeine缓存
        Object object = caffeineCache.getIfPresent(key);
        if (Objects.nonNull(object)) {
            log.info("查询Caffeine缓存，key={}", key);
            return object;
        }

        // 一级缓存Caffeine中不存在，查询二级缓存Redis
        String redisKey = this.cacheName + ":" + key;
        object = redisTemplate.opsForValue().get(redisKey);
        if (Objects.nonNull(object)) {
            // 写入Caffeine缓存
            caffeineCache.put(key, object);
            log.info("查询Redis缓存，redisKey={}，同时写入Caffeine缓存，caffeineKey={}", redisKey, key);
        }
        return object;
    }

    /**
     * 写入缓存
     *
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     */
    @Override
    public void put(Object key, Object value) {
        // 是否允许缓存null，允许缓存null的好处之一是可以避免缓存穿透
        if (!isAllowNullValues() && Objects.isNull(value)) {
            log.error("不能缓存null值，caffeineKey={}", key);
            return;
        }

        /**
         * Caffeine是不能直接缓存null的
         * 因此可以使用父类 {@link AbstractValueAdaptingCache#toStoreValue(Object)} 方法
         * 在value为null时，将它包装成一个NullValue.INSTANCE，也就是NullValue类型，解决Caffeine不能缓存null的问题
         */
        caffeineCache.put(key, toStoreValue(value));
        log.info("写入Caffeine缓存，caffeineKey={}", key);

        // null只在Caffeine中缓存一份即可，不用缓存在Redis
        if (Objects.isNull(value)) {
            return;
        }

        String redisKey = cacheName + ":" + key;
        // 获取配置文件的自定义key的过期时间的Map的Optional
        Optional<Map<String, Long>> allCustomizeRedisExpireOptional = Optional.ofNullable(doubleCacheConfigInfo).map(DoubleCacheConfigInfo::getAllCustomizeRedisExpire);
        // 获取配置文件的Redis缓存默认过期时间的Optional
        Optional<Long> redisExpireOptional = Optional.ofNullable(doubleCacheConfigInfo).map(DoubleCacheConfigInfo::getRedisExpire);
        // 优先根据自定义key的过期时间，其次是根据Redis缓存默认过期时间，都没有则不设置过期时间
        if (allCustomizeRedisExpireOptional.isPresent() && allCustomizeRedisExpireOptional.get().containsKey(redisKey)) {
            redisTemplate.opsForValue().set(redisKey, toStoreValue(value), allCustomizeRedisExpireOptional.get().get(redisKey), TimeUnit.SECONDS);
        } else if (redisExpireOptional.isPresent()) {
            redisTemplate.opsForValue().set(redisKey, toStoreValue(value), redisExpireOptional.get(), TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(redisKey, toStoreValue(value));
        }
        log.info("写入Redis缓存，redisKey={}", redisKey);
    }

    /**
     * 删除缓存
     *
     * @param key the key whose mapping is to be removed from the cache
     */
    @Override
    public void evict(Object key) {
        String redisKey = cacheName + ":" + key;
        redisTemplate.delete(redisKey);
        caffeineCache.invalidate(key);
        log.info("删除Caffeine和Redis缓存，caffeineKey={}，redisKey={}", key, redisKey);
    }

    /**
     * 清空缓存中所有数据
     */
    @Override
    public void clear() {
        Set<String> keys = redisTemplate.keys(cacheName.concat(":*"));
        for (String key : keys) {
            redisTemplate.delete(key);
        }
        caffeineCache.invalidateAll();
        log.info("清空Caffeine的 cacheName={} 所有缓存和Redis的 {} 所有缓存", cacheName, cacheName.concat(":*"));
    }

    /**
     * 通过key获取缓存值，如果没有找到，会调用valueLoader的call()方法
     * 如果只是使用注解来操作缓存的话，那么这个方法不会被使用到
     * 注意: 这个方法的接口注释中强调了需要我们自己来保证方法同步，因此这里使用了ReentrantLock进行了加锁操作
     *
     * @param key         the key whose associated value is to be returned
     * @param valueLoader
     * @param <T>
     * @return
     */
    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        ReentrantLock lock = new ReentrantLock();
        try {
            //加锁
            lock.lock();
            // 查询缓存
            Object object = lookup(key);
            if (Objects.nonNull(object)) {
                return (T) object;
            }
            //没有找到
            object = valueLoader.call();
            //写入缓存
            put(key, object);
            return (T) object;
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            lock.unlock();
        }
        return null;
    }

    /**
     * 获取缓存名称，一般是CacheManager在新建Cache的时候指定cacheName
     *
     * @return
     */
    @Override
    public String getName() {
        return cacheName;
    }

    /**
     * 获取实际使用的缓存
     *
     * @return
     */
    @Override
    public Object getNativeCache() {
        return this;
    }
}