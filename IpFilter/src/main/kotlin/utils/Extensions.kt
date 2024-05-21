package org.ip.filter.utils

import org.ip.filter.solutionsForDiscussions.Matrix

fun Matrix<Short>.increment(i: Int, j: Int, k: Int, l: Int) =
    set(i, j, k, l, ((get(i, j, k, l)?.let { it + 1 } ?: 1).toShort()))

fun String.insertStringAtIndex(string: String, index: Int) =
    StringBuilder(this).apply { insert(index, string) }.toString()

fun String.createOutputFileNameInput() =
    insertStringAtIndex("_out", indexOfLast { it == '.' })

object HashCode {
    private const val FILE_BUCKETS_COUNT = 1000
    const val IN_MEMORY_DATA_BATCH_SIZE = 100
    private const val HASH_CODE_DISTRIBUTION = Int.MAX_VALUE / FILE_BUCKETS_COUNT
    fun String.customHashCode() =
        1 + (hashCode() / HASH_CODE_DISTRIBUTION)
}

fun String.convertIPtoLongID() : Long {
    var id = 0L
    var div = 0L
    var dataIndex = 0
    toCharArray().forEach { c ->
        if (c == '.') {
            ++dataIndex
            id = (id shl 10) - (id shl 5) + (id shl 3) + div
            div = 0
        } else {
            // multiply on ten then add current char value to int conversion
            div = (div shl 3) + (div shl 1) + (c.code - '0'.code)
        }
    }
    id = (id shl 10) - (id shl 5) + (id shl 3) + div
    return id
}

fun Long.convertIDToIP() : String {
    val data = IntArray(4)
    var id = this
    for (i in 3 downTo 0) {
        data[i] = (id and 0b11111111).toInt()
        id = id shr 8
    }
    return data.joinToString(separator = ".")
}
