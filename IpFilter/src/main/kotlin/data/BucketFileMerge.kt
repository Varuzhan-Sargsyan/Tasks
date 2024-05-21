package org.ip.filter.data

import kotlinx.coroutines.*
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.concurrent.Executors

class BucketFileMerge(private val pathToTempFolder: String) {
    private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
    private lateinit var writer: FileWriter

    fun hashAllFileDataAndSaveToAndCount(output: String) {
        val folder = File(pathToTempFolder)
        val files = folder.listFiles() ?: return

        val outputFile = File(output)
        if (outputFile.exists()) {
            if (!outputFile.delete())
                throw Exception("Failed to delete file $outputFile")
        }
        if (!outputFile.createNewFile())
            throw Exception("Failed to create file $outputFile")

        writer = FileWriter(outputFile, true)
        runBlocking {
            val hashedCount = files.map { file ->
                async(dispatcher) { hashDataFile(writer, FileReader(file), file) }
            }.awaitAll().sum()
            writer.close()
            println("Final $hashedCount IP Addresses!!!!!!!!!!!")
        }
    }

    private suspend fun hashDataFile(writer: FileWriter, reader: FileReader, dataFile: File) : Int {
        delay(10)
        val hashSet = HashSet<String>()
        val builder = StringBuilder()
        reader.forEachLine { line ->
            if (line.trim().isEmpty())
                return@forEachLine

            if (hashSet.add(line))
                builder.append("$line\n")
        }

        val count = hashSet.size
        val name = dataFile.name

        synchronized(writer) {
            writer.append(builder.toString())
            writer.flush()
            reader.close()
            dataFile.delete()
        }

        hashSet.clear()

        println("File $name is hashed and merged with $count lines")

        return count
    }
}