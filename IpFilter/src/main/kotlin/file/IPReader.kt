package org.ip.filter.file

import org.ip.filter.data.IPAddress
import java.io.File

class IPReader {

    fun startReadingAddressesOneByOne(
        fileName: String,
        onIPAddress: (IPAddress) -> Unit,
        onFailed: (String) -> Unit
    ) {
        val file = File(fileName)
        file.forEachLine { line ->
            try {
                onIPAddress(IPAddress(line))
            } catch (e: Exception) {
                onFailed("Parsing failed for $line.\nException: ${e.message}")
            }
        }
    }

    fun startReadingLineOneByOne(
        fileName: String,
//        limit: Int = 0,
        onLine: (String) -> Unit,
        onFailed: (String) -> Unit
    ) {
//        var count = 0
        val file = File(fileName)
        file.forEachLine { line ->
//            ++count
//            if (limit != 0 && limit < count)
//                return@forEachLine
            try {
                onLine(line)
            } catch (e: Exception) {
                onFailed("Parsing failed for $line.\nException: ${e.message}")
            }
        }
    }

}