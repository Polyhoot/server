/*
 * Copyright (C) 2022 The Polyhoot Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ciphen.polyhoot.utils

import net.ciphen.polyhoot.Application
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.DateFormat
import java.util.*
import kotlin.system.exitProcess

object Log {
    private const val TAG = "Log"

    private val logFileName: String
    private val file: FileOutputStream
    private val writer: OutputStreamWriter

    init {
        val logFolder = File("logs")
        if (!logFolder.exists()) {
            logFolder.mkdir()
        }
        logFileName = "log-${System.currentTimeMillis()}.txt"
        file = FileOutputStream("logs/$logFileName")
        writer = file.writer()
    }

    private fun write(line: String) {
        println(line)
        writer.write(line + "\n")
    }

    fun d(tag: String, message: String) {
        if (Application.getInstance().applicationConfig.debug)
            message.split("\n").forEach {
                write("${getTimeDate()} D $tag: $it")
            }
    }

    fun e(tag: String, message: String) {
        message.split("\n").forEach {
            write("${getTimeDate()} E $tag: $it")
        }
    }

    fun f(tag: String, message: String) {
        message.split("\n").forEach {
            write("${getTimeDate()} F $tag: $it")
        }
        write("${getTimeDate()} F $TAG: FATAL: Can't continue. Exiting with error code 1")
        exitProcess(1)
    }

    fun i(tag: String, message: String) {
        message.split("\n").forEach {
            write("${getTimeDate()} I $tag: $it")
        }
    }

    private fun getTimeDate(): String = DateFormat.getDateTimeInstance().format(Date())

    fun onDestroy() {
        writer.close()
    }
}