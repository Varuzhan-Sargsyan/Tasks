package org.ip.filter.data

import kotlinx.coroutines.*
import java.io.File
import java.util.concurrent.Executors

class FolderFiles {
    // Create a custom thread pool
    private val customDispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()

    // Method to read all files in a folder and count lines using coroutines
    private suspend fun readFilesInFolder(folderName: String): Long {
        val folder = File(folderName)
        val files = folder.listFiles() ?: return 0

        return coroutineScope {
            files.map { file ->
                async(customDispatcher) { readFileLinesCount(file) }
            }.awaitAll().sum()
        }
    }

    // Method to read file lines count
    private suspend fun readFileLinesCount(file: File): Long {
        return withContext(customDispatcher) {
            var count = 0L
            file.forEachLine { _ -> ++count }
            println("File ${file.name} has $count lines")
            count
        }
    }

    // Method to read the line count of a single source file
    private suspend fun linesInSourceFile(fileName: String) : Long {
        val file = File(fileName)
        return withContext(customDispatcher) {
            var count = 0L
            file.forEachLine { _ ->
                ++count
                if (count % 10_000_000 == 0L) {
                    println("Read $count lines so far...")
                }
            }
            count
        }
    }


    // Method to compare lines count in source file with all lines in distributed files
    fun compareLinesCount(folderName: String, sourceFileName: String) {
        runBlocking {
            val totalLinesInFolder = readFilesInFolder(folderName)
            println("Total lines in all files: $totalLinesInFolder")

            val totalLinesInSourceFile = linesInSourceFile(sourceFileName)
            println("Total lines in source file: $sourceFileName: $totalLinesInSourceFile")

            println("Difference: ${totalLinesInSourceFile - totalLinesInFolder}")
        }
    }
}
