package com.joy.apm

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.joy.apm.common.Utils
import com.joy.apm.logcatReader.LogcatReaderHelper
import com.joy.apm.looperMonitor.LooperMonitorHelper
import com.joy.apm.strictMode.StrictModeHelper
import com.joy.apm.threadSampler.ThreadSampler

class Installer : ContentProvider() {
    override fun onCreate(): Boolean {
        if (!KitSwitch.enable) return true
        Utils.application = context as Application

        if (KitSwitch.bStrictMode) StrictModeHelper.init()
        if (KitSwitch.bLogcatReader) LogcatReaderHelper.init()
        if (KitSwitch.bLooperMonitor) LooperMonitorHelper.init()
        if (KitSwitch.bThreadSampler) ThreadSampler().start()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int {
        return 0
    }
} 