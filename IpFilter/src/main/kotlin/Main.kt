package org.ip.filter

import org.ip.filter.data.*
import org.ip.filter.file.IPReader
import utils.TimeCounter
import kotlin.system.exitProcess

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val input = "c:/res/ip_addresses_10000000_lines.txt"
    val output = "c:/res/ip_addresses_10000000_lines_filtered.txt"
    val tempFolder = "c:/res/temp"

    val timeCounter = TimeCounter().start()

    distributeIPAddressesToBatchedFiles(fileName = input, tempFolder = tempFolder)
    mergeDistributedFiles(output = output, tempFolder = tempFolder)

    timeCounter.end()
    println(timeCounter.toString())

//    runBlocking {
//        val dispatcher = Executors.newWorkStealingPool().asCoroutineDispatcher()
////        (1..20).map { async(dispatcher) { fileCopy(50_000_000L * it) } }.forEach { it.await() }
//        listOf (
//            async(dispatcher) { fileCopy(1_000_000) },
//            async(dispatcher) { fileCopy(10_000_000) },
//            async(dispatcher) { fileCopy(50_000_000) },
//            async(dispatcher) { fileCopy(100_000_000) },
//            async(dispatcher) { fileCopy(500_000_000) },
//            async(dispatcher) { fileCopy(1_000_000_000) },
//        ).forEach { it.await() }
//
//    }

    exitProcess(0)
}

fun mergeDistributedFiles(output: String, tempFolder: String) {
    BucketFileMerge(tempFolder).hashAllFileDataAndSaveToAndCount(output)

}

fun distributeIPAddressesToBatchedFiles(fileName: String, tempFolder: String) {
    val writer = BucketFileDistribution(tempFolder)
    val reader = IPReader()

    var count = 0L

    reader.startReadingLineOneByOne(
        fileName = fileName,
        onLine = { ip ->
            writer.addData(ip)
            ++count
            if (count % 10_000_000 == 0L)
                println("Read $count lines")
        },
        onFailed = { message ->
            println(message)
        }
    )

    writer.waitTillCloseIsDone()
    println("Distribution is done for $count IP addresses")
}

fun checkFileLines(fileName: String, tempFolder: String) {
    val folderFiles = FolderFiles()
    folderFiles.compareLinesCount(tempFolder, fileName)
}