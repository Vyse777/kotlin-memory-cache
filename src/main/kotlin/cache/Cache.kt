package cache

interface Cache<KeyType, ValueType> {
    val size: Int

    fun insert(key: KeyType, value: ValueType)

    fun get(key: KeyType): ValueType?

    fun remove(key: KeyType): ValueType?

    fun clear()
}
