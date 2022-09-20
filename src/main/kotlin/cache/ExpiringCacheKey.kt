package cache

class ExpiringCacheKey<KeyType>(val key: KeyType, var expireTimeInSeconds: Long = 0) {
    override fun hashCode(): Int {
        return key.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false

        other as ExpiringCacheKey<*>
        if (key != other.key) return false
        return true
    }
}
