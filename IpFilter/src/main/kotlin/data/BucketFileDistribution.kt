package org.ip.filter.data

import kotlinx.coroutines.*
import org.ip.filter.utils.HashCode.IN_MEMORY_DATA_BATCH_SIZE
import org.ip.filter.utils.HashCode.customHashCode
import java.io.File
import java.io.FileWriter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

class BucketFileDistribution(private val pathToTempFolder: String) {
    private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    private val dataMap = ConcurrentHashMap<Int, MutableList<String>>()
    private val fileMap = ConcurrentHashMap<Int, Pair<File, FileWriter>>()

    fun addData(data: String) {
        val hashCode = data.customHashCode()
        val dataList = dataMap.computeIfAbsent(hashCode) { mutableListOf() }
        synchronized(dataList) {
            dataList.add(data)
        }
        if (dataMap[hashCode]!!.size >= IN_MEMORY_DATA_BATCH_SIZE) {
            saveBatch(hashCode)
        }
    }

    private fun saveBatch(hashCode: Int) {
        val dataList = dataMap[hashCode] ?: return
        val file = fileMap.getOrPut(hashCode) {
            val file = File("$pathToTempFolder/$hashCode.txt")
            if (file.exists()) {
                if (!file.delete())
                    throw Exception("Failed to delete file $file")
            }
            if (!file.createNewFile())
                throw Exception("Failed to create file $file")
            Pair(file, FileWriter(file, true))
        }
        scope.launch {
            withContext(NonCancellable) {
                synchronized(dataList) {
                    if (dataList.isNotEmpty()) {
                        dataList.forEach { data ->
                            file.second.append("$data\n")
                        }
                        file.second.flush()
                        dataList.clear()
                    }
                }
            }
        }
    }

    private suspend fun closeAll() {
        var count = 0L
        // Save any remaining data in dataMap
        scope.coroutineContext[Job]?.children?.toList()?.joinAll()
        dataMap.keys.forEach { hashCode ->
            saveBatch(hashCode)
            delay(10)
            ++count
            if (count % 1000 == 0L)
                println("Saved $count batches")
        }
        // Wait for all file writing coroutines to complete
        scope.coroutineContext[Job]?.children?.toList()?.joinAll()
        // Close all file writers
        synchronized(fileMap) {
            fileMap.values.forEach { file ->
                file.second.flush()
                file.second.close()
            }
        }
    }

    // implement a method to wait till close is done
    fun waitTillCloseIsDone() {
        println("Waiting for files closing to be done...")
        runBlocking {
            closeAll()
        }
        println("Files closing is done!")
    }
}