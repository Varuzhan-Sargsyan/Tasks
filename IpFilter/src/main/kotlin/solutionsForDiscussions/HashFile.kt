package org.ip.filter.solutionsForDiscussions

import kotlinx.coroutines.*
import org.ip.filter.data.IPAddress
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

class HashFile {
    companion object{
        const val TOTAL_SIZE = 256L * 256 * 256 * 256 // Total size in bytes
        const val FILE_READING_PARTS = 10 // Total size in bytes
    }

    private lateinit var randomAccessFile: RandomAccessFile
    private val fileWriterThreadPool = Executors.newCachedThreadPool()//newFixedThreadPool(20)//newFixedThreadPoolContext(10, "HashFileWrapper")
    private val fileWriterThreadPoolDispatcher = fileWriterThreadPool.asCoroutineDispatcher()
    private val scope = CoroutineScope(SupervisorJob() + fileWriterThreadPoolDispatcher)

    fun createFileWithZeros(filePath: String) {
        val file = File(filePath)
        try {
            FileOutputStream(file).use { fos ->
                val buffer = ByteArray(1024 * 1024) // 1 MB buffer
                var bytesWritten: Long = 0

                while (bytesWritten < TOTAL_SIZE) {
                    val bytesToWrite = minOf(buffer.size.toLong(), TOTAL_SIZE - bytesWritten)
                    fos.write(buffer, 0, bytesToWrite.toInt())
                    bytesWritten += bytesToWrite
                    println("Written: $bytesWritten bytes")
                }
                println("File creation completed. Total bytes written: $bytesWritten")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        randomAccessFile = RandomAccessFile(filePath, "rw")
    }

    private fun incrementByteByOne(indexOfByte: Long) {
        synchronized(randomAccessFile) {
            randomAccessFile.seek(indexOfByte)
            val byteValue = randomAccessFile.read().toByte()
            randomAccessFile.seek(indexOfByte)
            randomAccessFile.writeByte(byteValue + 1)
        }
    }

    fun getByte(indexOfByte: Long): Byte {
        synchronized(randomAccessFile) {
            randomAccessFile.use { raf ->
                raf.seek(indexOfByte)
                return raf.read().toByte()
            }
        }
    }

//    fun incrementMatrixItemByOne(indices: IntArray) {
//        incrementByteByOne(to1DIndex(indices[0], indices[1], indices[2], indices[3]))
//    }

    fun to1DIndex(indices: ByteArray): Long {
        return (indices[0].toLong() and 0xFF) or
                ((indices[1].toLong() and 0xFF) shl 8) or
                ((indices[2].toLong() and 0xFF) shl 16) or
                ((indices[3].toLong() and 0xFF) shl 24)
    }

    fun to4DIndices(index: Long): ByteArray {
        return byteArrayOf(
            (index and 0xFF).toByte(),
            ((index shr 8) and 0xFF).toByte(),
            ((index shr 16) and 0xFF).toByte(),
            ((index shr 24) and 0xFF).toByte()
        )
    }

    private suspend fun readBytesInRange(file: File, start: Long, end: Long, callback: (Long, Byte) -> Unit) {
        file.inputStream().use { inputStream ->
            inputStream.skip(start)
            var index = start
            while (index < end) {
                val byte = inputStream.read().toByte()
                if (byte != 0.toByte()) {
                    callback(index, byte)
                }
                index++
            }
        }
    }

    fun readFile(fileName: String, onData: (IPAddress, Byte) -> Unit) = runBlocking {
        val file = File(fileName)
        val totalSize = file.length()
        val numParts = FILE_READING_PARTS // Change this to the number of parts you want to divide the file into

        val partSize = totalSize / numParts
        val startIndexes = (0 until numParts).map { it * partSize }
        val endIndexes = startIndexes.mapIndexed { index, startIndex ->
            if (index == numParts - 1) totalSize else (startIndex + partSize)
        }

        val byteCounter = AtomicLong(0)

        val jobs = (0 until numParts).map { index ->
            scope.launch {
                readBytesInRange(file, startIndexes[index], endIndexes[index]) { byteIndex, byteValue ->
                    // Process the byte value here
                    onData(IPAddress().update(to4DIndices(byteIndex)), byteValue)
                    byteCounter.incrementAndGet()
                }
            }
        }
        jobs.joinAll()

        println("Total non-zero bytes read: ${byteCounter.get()}")
        println("Total bytes sum : ${byteCounter.get()}")
    }

    fun updateHashData(ip: String) {
        scope.launch {
            incrementByteByOne(to1DIndex(IPAddress(ip).data))
        }
    }
}