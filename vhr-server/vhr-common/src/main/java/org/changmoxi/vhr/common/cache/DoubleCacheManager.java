package org.changmoxi.vhr.common.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.changmoxi.vhr.common.info.DoubleCacheConfigInfo;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 自定义缓存管理器，管理自定义缓存组件{@link DoubleCache}
 * CacheManager接口是一个缓存管理器，它可以用来管理一组Cache，比如CaffeineCacheManager、RedisCacheManager 都实现了这个接口
 *
 * @author CZS
 * @create 2023-02-24 15:53
 **/
public class DoubleCacheManager implements CacheManager {
    /**
     * 使用ConcurrentHashMap维护一组不同的Cache
     */
    Map<String, Cache> cacheMap = new ConcurrentHashMap<>();
    private RedisTemplate<String, Object> redisTemplate;
    private DoubleCacheConfigInfo doubleCacheConfigInfo;

    public DoubleCacheManager(RedisTemplate<String, Object> redisTemplate, DoubleCacheConfigInfo doubleCacheConfigInfo) {
        this.redisTemplate = redisTemplate;
        this.doubleCacheConfigInfo = doubleCacheConfigInfo;
    }

    /**
     * 根据name获取Cache实例，不存在则创建
     *
     * @param name 相当于 {@link DoubleCache#cacheName} 属性，CacheManager根据name实现不同Cache的隔离。
     *             不存在name对应的Cache实例时，会将name传入DoubleCache的构造器{@link DoubleCache#DoubleCache(String, DoubleCacheConfigInfo, com.github.benmanes.caffeine.cache.Cache, RedisTemplate)}
     *             作为cacheName来创建一个DoubleCache也就是Cache实例。
     * @return
     */
    @Override
    public Cache getCache(String name) {
        // 根据name查找对应的Cache
        Cache cache = cacheMap.get(name);
        if (Objects.nonNull(cache)) {
            return cache;
        }
        // 没有对应的Cache，则新建一个以name为cacheName的DoubleCache，使用ConcurrentHashMap的putIfAbsent方法放入，避免重复创建Cache以及造成Cache内数据的丢失
        cache = new DoubleCache(name, doubleCacheConfigInfo, createCaffeineCache(), redisTemplate);
        Cache oldCache = cacheMap.putIfAbsent(name, cache);
        // 如果没有name对应的Cache，则放入新建的Cache，并且返回null，也就是oldCache为null
        // 如果已经有name对应的Cache，则不会覆盖已有的Cache，并且返回已有的Cache，也就是oldCache为已有的Cache
        return Objects.isNull(oldCache) ? cache : oldCache;
    }

    /**
     * 返回管理的所有cacheName
     *
     * @return
     */
    @Override
    public Collection<String> getCacheNames() {
        return cacheMap.keySet();
    }

    /**
     * 创建Caffeine的Cache，根据配置文件的具体参数进行初始化
     *
     * @return
     */
    private com.github.benmanes.caffeine.cache.Cache createCaffeineCache() {
        Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder();
        // 构造以传入的doubleCacheInfo为value的Optional，如果doubleCacheInfo为null，构造出来的就是Optional.EMPTY，否则就是包含doubleCacheInfo的Optional
        Optional<DoubleCacheConfigInfo> doubleCacheInfoOptional = Optional.ofNullable(doubleCacheConfigInfo);
        // map: 如果当前Optional为Optional.EMPTY，则依旧返回Optional.EMPTY，否则返回包含 传入的操作的结果 的Optional
        // 比如 map(DoubleCacheConfigInfo::getInitialCapacity) 相当于 map(value -> value.getInitialCapacity())，也就是获取doubleCacheInfo的initialCapacity对应的Optional
        // ifPresent: 如果当前Optional的value值非空，就执行传入的操作
        doubleCacheInfoOptional.map(DoubleCacheConfigInfo::getInitialCapacity)
                .ifPresent(initialCapacity -> caffeineBuilder.initialCapacity(initialCapacity));
        doubleCacheInfoOptional.map(DoubleCacheConfigInfo::getMaximumSize)
                .ifPresent(maximumSize -> caffeineBuilder.maximumSize(maximumSize));
        doubleCacheInfoOptional.map(DoubleCacheConfigInfo::getExpireAfterWrite)
                .ifPresent(expireAfterWrite -> caffeineBuilder.expireAfterWrite(expireAfterWrite, TimeUnit.SECONDS));
        doubleCacheInfoOptional.map(DoubleCacheConfigInfo::getExpireAfterAccess)
                .ifPresent(expireAfterAccess -> caffeineBuilder.expireAfterAccess(expireAfterAccess, TimeUnit.SECONDS));
        doubleCacheInfoOptional.map(DoubleCacheConfigInfo::getRefreshAfterWrite)
                .ifPresent(refreshAfterWrite -> caffeineBuilder.refreshAfterWrite(refreshAfterWrite, TimeUnit.SECONDS));
        return caffeineBuilder.build();
    }
}