package org.ip.filter.solutionsForDiscussions

import kotlinx.coroutines.*
import org.ip.filter.utils.HashCode.customHashCode
import java.io.File
import java.io.FileWriter
import java.util.concurrent.Executors

class DistributionByHashCode(private val pathToTempFolder: String) {

    private val fileWriterThreadPool = Executors.newCachedThreadPool()//newFixedThreadPool(20)//newFixedThreadPoolContext(10, "HashFileWrapper")
    private val fileWriterThreadPoolDispatcher = fileWriterThreadPool.asCoroutineDispatcher()
    private val scope = CoroutineScope(SupervisorJob() + fileWriterThreadPoolDispatcher)

    private val map = HashMap<Int, FileWriter>()

    private val lockFileCreation = Any()

    private fun fileWriter(hashCode: Int) : FileWriter {
        synchronized(lockFileCreation) {
            map[hashCode]?.let { return it }
            val file = File("$pathToTempFolder/$hashCode.txt")

            if (file.exists()) {
                if (!file.delete())
                    throw Exception("Failed to delete file $file")
            }
            if (!file.createNewFile())
                throw Exception("Failed to create file $file")

            val fileWriter = FileWriter(file, true)
            map[hashCode] = fileWriter
            return fileWriter
        }
    }

    fun mapToFile(ip: String) {
        scope.launch(Dispatchers.Default) {
            withContext(Dispatchers.IO) {
                fileWriter(ip.customHashCode()).append("$ip\n")
            }
        }
    }

    fun mappingToFileIsDone(onDone : () -> Unit) {
        scope.launch {
            // Wait for all file writing coroutines to complete
            scope.coroutineContext[Job]?.children?.toList()?.joinAll()
            // Close all file writers
            synchronized(map) {
                map.values.forEach { it.close() }
            }
            // Invoke the callback
            onDone()
        }
    }
}

