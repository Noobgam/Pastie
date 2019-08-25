package me.noobgam.pastie.main.background.cache;

import me.noobgam.pastie.main.background.Scheduler;

import java.util.Map;

/**
 * Simple cache map wrapper.
 * User should himself ensure map is modifiable
 */
public abstract class SimpleMapCache<K, V> extends AbstractCachedSomething<Map<K, V>> {

    /**
     * @param scheduler scheduler to add a job to
     */
    protected SimpleMapCache(Scheduler scheduler) {
        super(scheduler);
    }

    // null is impossible here, this operation is synchronized with UpdateCacheJob
    @SuppressWarnings("ConstantConditions")
    public synchronized V put(K key, V val) {
        if (value == null) {
            throw new IllegalArgumentException("Cache was not instantiated yet.");
        }
        return value.put(key, val);
    }

    // null is impossible here, this operation is synchronized with UpdateCacheJob
    @SuppressWarnings("ConstantConditions")
    public synchronized V remove(K key) {
        if (value == null) {
            throw new IllegalArgumentException("Cache was not instantiated yet.");
        }
        return value.remove(key);
    }
}
