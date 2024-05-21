package org.ip.filter.data

class IPAddress(private var ip: String = "0.0.0.0") {
    var data: ByteArray

    init {
        data = ipAddressToIndices()
    }

    fun update(ip: String) : ByteArray {
        this.ip = ip
        data = ipAddressToIndices()
        return data
    }

    fun update(data: ByteArray) : IPAddress {
        this.data = data
        ip = dataToString()
        return this
    }

    fun data(index: Int) = data[index]
    fun data() = ip

    private fun dataToString() = data.joinToString(separator = ".")

    fun line() = ip + "\n"

    private fun ipAddressToIndices(): ByteArray {
        val data = ByteArray(4)

        try {
            data[0] = 0
            data[1] = 0
            data[2] = 0
            data[3] = 0

            var dataIndex = 0
            ip.toCharArray().forEach { c ->
                if (c == '.') {
                    ++dataIndex
                } else {
                    // multiply on ten then add current char value to int conversion
                    data[dataIndex] = ((data[dataIndex].toInt() shl 3) + (data[dataIndex].toInt() shl 1) + (c.code - '0'.code)).toByte()
                }
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
        return data
    }
}