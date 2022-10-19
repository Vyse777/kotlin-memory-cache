package cache

import MILLISECONDS_IN_SECOND

class ExpiringInMemoryCache<KeyType, ValueType>(
    override val defaultExpiryTimeInSeconds: Int
) : ExpiringCache<KeyType, ValueType> {
    private val cache: MutableMap<KeyType, ValueType> = mutableMapOf()
    private var cacheExpiryTime: Long = calculateExpiryTime()

    private fun calculateExpiryTime() = System.currentTimeMillis() / MILLISECONDS_IN_SECOND + defaultExpiryTimeInSeconds

    @Synchronized
    private fun checkCacheExpiry() {
        if (cacheExpiryTime <= System.currentTimeMillis() / MILLISECONDS_IN_SECOND) {
            clear()
            cacheExpiryTime = calculateExpiryTime()
        }
    }

    @Synchronized
    override fun getOrDefault(key: KeyType, default: ValueType?): ValueType? {
        checkCacheExpiry()

        return cache.getOrDefault(key, default)
    }

    @Synchronized
    override fun insertOrUpdate(key: KeyType, value: ValueType, expiryTimeInSeconds: Int?, updateExpiryTime: Boolean) {
        checkCacheExpiry()

        cache[key] = value
    }

    @Synchronized
    fun putAll(from: Map<KeyType, ValueType>) {
        checkCacheExpiry()

        cache.putAll(from)
    }

    @Synchronized
    fun checkAllKeysInCache(keys: Iterable<KeyType>): Boolean {
        checkCacheExpiry()

        return cache.keys.isNotEmpty() && keys.all { cache.keys.contains(it) }
    }

    @Synchronized
    fun getAll(): MutableCollection<ValueType> {
        checkCacheExpiry()

        return cache.values
    }

    override val size: Int get() = cache.size

    @Synchronized
    override fun clear() {
        cache.clear()
    }

    @Synchronized
    override fun remove(key: KeyType): ValueType? {
        checkCacheExpiry()

        return cache.remove(key)
    }
}
