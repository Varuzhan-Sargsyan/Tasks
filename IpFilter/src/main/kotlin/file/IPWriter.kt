package org.ip.filter.file

import org.ip.filter.data.IPAddress
import org.ip.filter.utils.createOutputFileNameInput
import java.io.File

class IPWriter(private val fileName: String) {
    private var file: File? = null

    fun createOutputFile() {
        file = null
        file = File(fileName).apply {
            if (exists()) {
                if (!delete())
                    throw Exception("Failed to delete file $fileName")
            }
            if (!createNewFile()) {
                throw Exception("Failed to create file $fileName")
            }
            println("File $fileName created")
        }
    }

    fun writeToFile(ipAddress: IPAddress) {
        file?.appendText(ipAddress.line())
    }

    fun writeToFile(line: String) {
        file?.appendText(line)
    }

    fun closeFile() {
        file = null
    }
}