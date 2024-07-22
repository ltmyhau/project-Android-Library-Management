package com.example.project_android_library_management

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Database_ThuVien.db"
        private const val DATABASE_VERSION = 1
    }

    fun openDatabase(): SQLiteDatabase {
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        if (!dbFile.exists()) {
            copyDatabaseFromAssets()
        }
        return SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READWRITE)
    }

    private fun copyDatabaseFromAssets() {
        val inputStream: InputStream = context.assets.open(DATABASE_NAME)
        val outputStream: OutputStream = FileOutputStream(context.getDatabasePath(DATABASE_NAME))
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
    }

    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}

