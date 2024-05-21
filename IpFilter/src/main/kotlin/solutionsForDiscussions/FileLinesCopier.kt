package org.ip.filter.solutionsForDiscussions

import java.io.File

class FileLinesCopier {
    // make a method that copies lines from one file to another
    // the method should accept two file names as arguments
    fun copyLines(source: String, destination: String, maxLines: Long) {
        val fromFile = File(source)
        val toFile = File(destination)

        fromFile.bufferedReader().use { reader ->
            toFile.bufferedWriter().use { writer ->
                var lines = 0

                var line = reader.readLine()
                while (line != null) {
                    ++lines
                    writer.appendLine(line)

                    if (lines % 10_000_000 == 0)
                        println("Copied $lines lines")

                    if (maxLines in 1..lines)
                        break

                    line = reader.readLine()
                }

                println("File copy is done! Copied $lines lines.")
            }
        }
    }
}