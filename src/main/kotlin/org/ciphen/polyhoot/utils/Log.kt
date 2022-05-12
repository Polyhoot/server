package org.ciphen.polyhoot.utils

import org.ciphen.polyhoot.Application
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.DateFormat
import java.util.Date
import kotlin.system.exitProcess

class Log {
    companion object {
        private val TAG = "Log"

        var logger: Log? = null
            get() {
                if (field == null) {
                    field = Log()
                }
                return field
            }
    }

    val logFileName: String
    private val file: FileOutputStream
    private val writer: OutputStreamWriter

    init {
        val logFolder = File("logs")
        if (!logFolder.exists()) {
            logFolder.mkdir()
        }
        logFileName = "log-${java.lang.System.currentTimeMillis()}.txt"
        file = FileOutputStream("logs/$logFileName")
        writer = file.writer()
    }

    private fun write(line: String) {
        println(line)
        writer.write(line + "\n")
    }

    fun D(tag: String, message: String) {
        if (Application.getInstance().applicationConfig.debug)
            message.split("\n").forEach {
                write("${getTimeDate()} D $tag: $it")
            }
    }

    fun E(tag: String, message: String) {
        message.split("\n").forEach {
            write("${getTimeDate()} E $tag: $it")
        }
    }

    fun F(tag: String, message: String) {
        message.split("\n").forEach {
            write("${getTimeDate()} F $tag: $it")
        }
        write("${getTimeDate()} F $TAG: FATAL: Can't continue. Exiting with error code 1")
        exitProcess(1)
    }

    fun I(tag: String, message: String) {
        message.split("\n").forEach {
            write("${getTimeDate()} I $tag: $it")
        }
    }

    private fun getTimeDate(): String = DateFormat.getDateTimeInstance().format(Date())

    fun onDestroy() {
        I(TAG, "Closing writer...")
        I(TAG, "Have a great day!")
        writer.close()
        logger = null
    }
}