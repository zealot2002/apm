package com.joy.apm.common

import android.annotation.SuppressLint
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class FileUtils {

    companion object {
        //所有开发工具的根目录
        private fun getRootDir(): File {
            var dir = Utils.application.getExternalFilesDir(null)
            if (Environment.MEDIA_MOUNTED != Environment.getExternalStorageState() || dir == null)
                dir = Utils.application.filesDir

            return File(dir, Utils.moduleName)
        }

        fun getKitDir(kitName: String): File {
            val parentDir = getRootDir()
            return File(parentDir, kitName)
        }

        /**
         * 日志为txt类型，且都以当前时间命名
         *
         * 不同kit的日志，在不同的文件夹内
         */
        fun getLogFile(kitDir: File): File {
            if (!kitDir.exists()) {
                kitDir.mkdirs()
            }
            val file = File(
                kitDir, getDateTimeString() + ".txt"
            )
            if (file.exists()) return file
            file.createNewFile()
            return file
        }

        fun getLogFile(kitDirPath: String, logName: String? = getDateTimeString()): File {
            val kitDir = getKitDir(kitDirPath)
            if (!kitDir.exists()) {
                kitDir.mkdirs()
            }
            val file = File(
                kitDir, "$kitDirPath$logName.txt"
            )
            if (file.exists()) return file
            file.createNewFile()
            return file
        }

        //删除非当天的logs
        fun delOldFiles(dir: File) {
            if (!dir.exists() || !dir.isDirectory) return
            val files = dir.listFiles()
            val today = getTodayString()
            if (!files.isNullOrEmpty()) for (file in files) {
                if (!file.path.contains(today)) file.delete()
            }
        }

        //删除非当天的logs
        fun delOldFiles(kitDirPath: String) {
            Thread {
                kotlin.runCatching {
                    val dir = getKitDir(kitDirPath)
                    if (!dir.exists() || !dir.isDirectory) return@runCatching
                    val files = dir.listFiles()
                    val today = getTodayString()
                    if (!files.isNullOrEmpty()) for (file in files) {
                        if (!file.path.contains(today)) file.delete()
                    }
                }
            }.start()
        }

        @SuppressLint("SimpleDateFormat")
        fun getDateTimeString(): String {
            val dateTime = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd HH-mm")
            return formatter.format(dateTime)
        }

        @SuppressLint("SimpleDateFormat")
        fun getTodayString(): String {
            val dateTime = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            return formatter.format(dateTime)
        }
    }
} 