package cache
import MILLISECONDS_IN_SECOND

class ExpiringEntryInMemoryCache<KeyType : Any, ValueType : Any>(
    override val defaultExpiryTimeInSeconds: Int
) : ExpiringCache<KeyType, ValueType> {
    private val cache: MutableMap<ExpiringCacheKey<KeyType>, ValueType> = mutableMapOf()

    private fun calculateExpireTime(expireTimeInSeconds: Int): Long {
        return System.currentTimeMillis() / MILLISECONDS_IN_SECOND + expireTimeInSeconds
    }

    @Synchronized
    private fun removeExpired() {
        for (key in cache.keys) {
            if (key.expireTimeInSeconds <= System.currentTimeMillis() / MILLISECONDS_IN_SECOND) {
                cache.remove(key)
            }
        }
    }

    override val size: Int
        get() {
            removeExpired()
            return cache.size
        }

    override fun insertOrUpdate(key: KeyType, value: ValueType, expiryTimeInSeconds: Int?, updateExpiryTime: Boolean) {
        removeExpired()

        val cacheKey =
            ExpiringCacheKey(key, this.calculateExpireTime(expiryTimeInSeconds ?: defaultExpiryTimeInSeconds))
        synchronized(this) {
            cache[cacheKey] = value
        }
    }

    override fun getOrDefault(key: KeyType, default: ValueType?): ValueType? {
        removeExpired()

        val cacheKey = ExpiringCacheKey(key)
        synchronized(this) {
            return cache[cacheKey]
        }
    }

    fun getAndUpdateExpireTimeOrDefault(key: KeyType, expiryTimeInSeconds: Int?, default: ValueType?): ValueType? {
        removeExpired()

        val cacheKey = ExpiringCacheKey(key)
        synchronized(this) {
            return if (cache.containsKey(cacheKey)) {
                cache.keys.first { it == cacheKey }.expireTimeInSeconds =
                    calculateExpireTime(expiryTimeInSeconds ?: defaultExpiryTimeInSeconds)
                cache[cacheKey]
            } else
                default
        }
    }

    override fun remove(key: KeyType): ValueType? {
        removeExpired()

        val cacheKey = ExpiringCacheKey(key)
        synchronized(this) {
            return cache.remove(cacheKey)
        }
    }

    @Synchronized
    override fun clear() {
        cache.clear()
    }
}
