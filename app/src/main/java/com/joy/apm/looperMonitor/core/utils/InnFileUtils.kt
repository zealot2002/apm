package com.joy.apm.looperMonitor.core.utils

import com.joy.apm.common.FileUtils
import com.joy.apm.looperMonitor.Cfg
import java.io.File
import java.io.FilenameFilter

class InnFileUtils {
    companion object {
        private fun getBlockLogDir(): File {
            return FileUtils.getKitDir(Cfg.kitDirPath)
        }

        private fun detectedBlockDirectory(): File {
            val dir = getBlockLogDir()
            if (!dir.exists()) {
                dir.mkdirs()
            }
            return dir
        }

        fun getLogFiles(): Array<File?>? {
            val f = detectedBlockDirectory()
            return if (f.exists() && f.isDirectory) {
                f.listFiles(BlockLogFileFilter())
            } else null
        }

        private class BlockLogFileFilter() : FilenameFilter {
            private val TYPE = ".log"
            override fun accept(dir: File, filename: String): Boolean {
                return filename.endsWith(TYPE)
            }
        }
    }
}