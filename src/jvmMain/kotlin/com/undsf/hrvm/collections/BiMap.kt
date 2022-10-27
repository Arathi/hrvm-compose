package com.undsf.hrvm.collections

import androidx.compose.runtime.mutableStateMapOf

class BiMap<K, V>(map: Map<K, V> = mapOf()) {
    private val keyToValue: MutableMap<K, V> = mutableStateMapOf()
    private val valueToKey: MutableMap<V, K> = mutableStateMapOf()

    val size: Int get() = keyToValue.size

    init {
        keyToValue.clear()
        valueToKey.clear()
        for (entry in map) {
            this[entry.key] = entry.value
        }
    }

    operator fun get(key: K): V? {
        return keyToValue[key]
    }

    operator fun set(key: K, value: V) {
        keyToValue[key] = value
        valueToKey[value] = key
    }

    fun getValueByKey(key: K): V? = get(key)

    fun getKeyByValue(value: V): K? {
        return valueToKey[value]
    }

    fun clear() {
        keyToValue.clear()
        valueToKey.clear()
    }
}