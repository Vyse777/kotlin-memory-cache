package cache

interface ExpiringCache<KeyType, ValueType> : Cache<KeyType, ValueType> {
    val defaultExpiryTimeInSeconds: Int

    override fun insert(key: KeyType, value: ValueType) {
        insertOrUpdate(key, value, defaultExpiryTimeInSeconds, true)
    }

    override fun get(key: KeyType): ValueType? {
        return getOrDefault(key, null)
    }

    fun insertOrUpdate(key: KeyType, value: ValueType, expiryTimeInSeconds: Int?, updateExpiryTime: Boolean)

    fun getOrDefault(key: KeyType, default: ValueType?): ValueType?
}
