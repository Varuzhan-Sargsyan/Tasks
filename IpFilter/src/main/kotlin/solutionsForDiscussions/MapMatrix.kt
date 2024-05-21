package org.ip.filter.solutionsForDiscussions

class MapMatrix {
    private val map = HashMap<Byte, HashMap<Byte, HashMap<Byte, HashMap<Byte, Byte>>>>()

    operator fun get(i: Byte, j: Byte, k: Byte, l: Byte) : Byte? = map[i]?.get(j)?.get(k)?.get(l)
    operator fun set(i: Byte, j: Byte, k: Byte, l: Byte, value: Byte) {
        if (map[i] == null) map[i] = HashMap()
        if (map[i]!![j] == null) map[i]!![j] = HashMap()
        if (map[i]!![j]!![k] == null) map[i]!![j]!![k] = HashMap()
        map[i]!![j]!![k]!![l] = value
    }
    fun increment(i: Byte, j: Byte, k: Byte, l: Byte) =
        set(i, j, k, l, (get(i, j, k, l)?.let { it + 1 } ?: 1).toByte())

    fun forEach(action: (ByteArray, Byte) -> Unit) {
        map.forEach { (i, jMap) ->
            jMap.forEach { (j, kMap) ->
                kMap.forEach { (k, lMap) ->
                    lMap.forEach { (l, value) ->
                        action(byteArrayOf(i, j, k, l), value)
                    }
                }
            }
        }
    }
}