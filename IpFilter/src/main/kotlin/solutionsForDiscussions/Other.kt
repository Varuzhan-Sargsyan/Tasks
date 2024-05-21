package org.ip.filter.solutionsForDiscussions

import kotlinx.coroutines.delay
import org.ip.filter.data.*
import org.ip.filter.file.IPReader
import org.ip.filter.file.IPWriter
import org.ip.filter.utils.createOutputFileNameInput
import utils.TimeCounter


fun hashFileBuild() {
    val sourceFileName = "C:/res/ip_addresses_100000000_lines.txt"
    val hashFileName = "C:/res/temp/ip_addresses_hash.txt"
    val writer = IPWriter(sourceFileName.createOutputFileNameInput())
    val hashFile = HashFile()
    val reader = IPReader()
    var count = 0L

    hashFile.createFileWithZeros(hashFileName)

    reader.startReadingLineOneByOne(
        fileName = sourceFileName,
        onLine = { ip ->
            hashFile.updateHashData(ip)
            ++count
            if (count % 100_000_000 == 0L)
                println("Read $count lines")
        },
        onFailed = { message ->
            println(message)
        }
    )

    hashFile.readFile(
        fileName = hashFileName,
        onData = { ip, repetitions ->
            writer.writeToFile("$ip\n")
            count -= repetitions
        },
    )

    writer.closeFile()
    println("Done! $count lost!")
}

suspend fun fileCopy(maxLines: Long) {
    delay(100)
    FileLinesCopier().copyLines(
        source = "C:/res/ip_addresses.txt",
        destination = "C:/res/ip_addresses_${maxLines}_lines.txt",
        maxLines = maxLines)
}

fun distributeIPAddressesToHashFiles() {
    val hashFile = DistributionByHashCode("C:/res/temp")
    val fileName = "C:/res/ip_addresses_100000000_lines.txt"
    val reader = IPReader()

    var count = 0

    reader.startReadingLineOneByOne(
        fileName = fileName,
        onLine = { ip ->
            hashFile.mapToFile(ip)
            ++count
        },
        onFailed = { message ->
            println(message)
        }
    )

    hashFile.mappingToFileIsDone { println("Done! $count") }
}

fun testHashDistribution() {
    val fileName = "C:/res/ip_addresses.txt"
    val reader = IPReader()

    val map = HashMap<Int, Int>()
    var count = 0

    reader.startReadingAddressesOneByOne(
        fileName = fileName,
        onIPAddress = { ipAddress ->
            val hashCode = ipAddress.hashCode() / 2147483
            if (map.containsKey(hashCode)) {
                map[hashCode] = map[hashCode]!! + 1
            } else {
                map[hashCode] = 1
            }
            ++count
        },
        onFailed = { message ->
            println(message)
        }
    )

}

fun runHashMapAlgorithm(fileName: String, method: String) {

    val timeCounter = TimeCounter()
    timeCounter.start()

    println("Started $method")

    val matrix = MapMatrix()

    println("Matrix created")

    val reader = IPReader()

    println("Reader created")

    reader.startReadingAddressesOneByOne(
        fileName = fileName,
        onIPAddress = { ipAddress ->
            matrix.increment(
                ipAddress.data(0),
                ipAddress.data(1),
                ipAddress.data(2),
                ipAddress.data(3)
            )
        },
        onFailed = { message ->
            println(message)
        }
    )

    println("Reading is done!")

    val writer = IPWriter(fileName)
    writer.createOutputFile()

    println("Writer created")

    val ipAddress= IPAddress()
    matrix.forEach { array, _ ->
        ipAddress.update(array)
        writer.writeToFile(ipAddress)
    }
    writer.closeFile()

    timeCounter.end()
    println(timeCounter.toString())

    println("Done!!!")
}


fun runArrayAlgorithm(fileName: String, method: String) {

    val timeCounter = TimeCounter()
    timeCounter.start()

    println("Started $method")

    val matrix = Matrix.buildIPAddressMatrixShort()

    println("Matrix created")

    val reader = IPReader()

    println("Reader created")

    reader.startReadingAddressesOneByOne(
        fileName = fileName,
        onIPAddress = { ipAddress ->
//            matrix.increment(
//                ipAddress.data(0),
//                ipAddress.data(1),
//                ipAddress.data(2),
//                ipAddress.data(3)
//            )
        },
        onFailed = { message ->
            println(message)
        }
    )

    println("Reading is done!")

    val writer = IPWriter(fileName)
    writer.createOutputFile()

    println("Writer created")

    val ipAddress= IPAddress()
    matrix.forEach { array, _ ->
        ipAddress.update(array)
        writer.writeToFile(ipAddress)
    }
    writer.closeFile()

    timeCounter.end()
    println(timeCounter.toString())

    println("Done!!!")
}

fun runHashSetAlgorithm(fileName: String, method: String) {

    val timeCounter = TimeCounter()
    timeCounter.start()

    println("Started $method")

    val matrix = HashMapMatrix()

    println("Matrix created")

    val reader = IPReader()

    println("Reader created")

    reader.startReadingLineOneByOne(
        fileName = fileName,
        onLine = { line ->
            matrix.add(line)
        },
        onFailed = { message ->
            println(message)
        }
    )

    println("Reading is done!")

    val writer = IPWriter(fileName)
    writer.createOutputFile()

    println("Writer created")

    matrix.forEach { line ->
        writer.writeToFile(line)
    }
    writer.closeFile()

    timeCounter.end()
    println(timeCounter.toString())

    println("Done!!!")
}