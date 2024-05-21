package org.ip.filter.solutionsForDiscussions

class Matrix<T> private constructor(
    d1: Int,
    d2: Int,
    d3: Int,
    d4: Int,
    private val initialization: ((Int, Int, Int, Int) -> T)? = null
) {
    private var matrix: Array<Array<Array<Array<Any?>>>> = Array(d1) { i1 ->
        Array(d2) { i2 ->
            Array(d3) { i3 ->
                Array(d4) { i4 -> null }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    operator fun get(i: Int, j: Int, k: Int, l: Int) : T? = matrix[i][j][k][l] as T?
    operator fun set(i: Int, j: Int, k: Int, l: Int, value: T?) {
        matrix[i][j][k][l] = value as Any?
    }

    fun forEach(action: (ByteArray, T) -> Unit) {
        matrix.forEachIndexed { i1, it1 ->
            it1.forEachIndexed { i2, it2 ->
                it2.forEachIndexed { i3, it3 ->
                    it3.forEachIndexed { i4, it4 ->
                        action(byteArrayOf(i1.toByte(), i2.toByte(), i3.toByte(), i4.toByte()), it4 as T)
                    }
                }
            }
        }
    }

    override fun toString() =
        matrix.joinToString(postfix = "\n") { it1 ->
            it1.joinToString(postfix = "\n") { it2 ->
                it2.joinToString(postfix = "\n") { it3 ->
                    it3.joinToString(separator = ", ", prefix = "\n")
                }
            }
        }

    companion object Builder {
        fun buildIPAddressMatrixShort() =
            Matrix<Short>(1000, 1000, 1000, 1000) { _, _, _, _ -> 0 }
        fun buildIPAddressMatrixByte() =
            Matrix<Byte>(255, 255, 255, 255) { _, _, _, _ -> 0 }
    }
}

