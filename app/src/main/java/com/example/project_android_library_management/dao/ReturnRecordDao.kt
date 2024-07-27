package com.example.project_android_library_management.dao

import android.content.ContentValues
import android.database.Cursor
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.ReturnRecord

class ReturnRecordDao(private val databaseHelper: DatabaseHelper) {

    fun generateNewId(): String {
        var newId = "PT00001"
        val db = databaseHelper.readableDatabase
        db.rawQuery("SELECT MAX(MaPT) FROM PhieuTra", null).use { cursor ->
            if (cursor.moveToFirst()) {
                val maxCode = cursor.getString(0)
                if (!maxCode.isNullOrEmpty()) {
                    val currentNumber = maxCode.substring(2).toInt()
                    val newNumber = currentNumber + 1
                    newId = "PT" + String.format("%05d", newNumber)
                }
            }
        }
        db.close()
        return newId
    }

    fun insert(returnRecord: ReturnRecord): Int {
        val db = databaseHelper.openDatabase()

        val contentValues = ContentValues().apply {
            put("MaPT", returnRecord.MaPT)
            put("NgayTra", returnRecord.NgayTra)
            put("TienPhat", returnRecord.TienPhat)
            put("MaPM", returnRecord.MaPM)
            put("MaTT", returnRecord.MaTT)
            put("MaDG", returnRecord.MaDG)
        }
        val result = db.insert("PhieuTra", null, contentValues)
        db.close()
        return if (result == -1L) 0 else 1
    }

    fun update(returnRecord: ReturnRecord): Int {
        val db = databaseHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put("NgayTra", returnRecord.NgayTra)
            put("TienPhat", returnRecord.TienPhat)
            put("MaPM", returnRecord.MaPM)
            put("MaTT", returnRecord.MaTT)
            put("MaDG", returnRecord.MaDG)
        }

        val rowsAffected = db.update("PhieuTra", contentValues, "MaPT = ?", arrayOf(returnRecord.MaPT))
        db.close()
        return rowsAffected
    }

    fun delete(maPM: String): Int {
        val db = databaseHelper.writableDatabase
        db.execSQL("PRAGMA foreign_keys = ON;")
        db.delete("CTPhieuTra", "MaPT = ?", arrayOf(maPM))
        val rowsAffected = db.delete("PhieuTra", "MaPT = ?", arrayOf(maPM))
        db.close()
        return rowsAffected
    }

    private fun cursor(cursor: Cursor): ReturnRecord {
        val maPT = cursor.getString(cursor.getColumnIndexOrThrow("MaPT"))
        val ngayTra = cursor.getString(cursor.getColumnIndexOrThrow("NgayTra"))
        val tienPhat = cursor.getDouble(cursor.getColumnIndexOrThrow("TienPhat"))
        val maPM = cursor.getString(cursor.getColumnIndexOrThrow("MaPM"))
        val maTT = cursor.getString(cursor.getColumnIndexOrThrow("MaTT"))
        val maDG = cursor.getString(cursor.getColumnIndexOrThrow("MaDG"))

        return ReturnRecord(maPT, ngayTra, tienPhat, maPM, maTT, maDG)
    }

    fun getAllReturnRecord(): ArrayList<ReturnRecord> {
        val returnRecord = ArrayList<ReturnRecord>()
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM PhieuTra", null)
        if (cursor.moveToFirst()) {
            do {
                returnRecord.add(cursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return returnRecord
    }

    fun getReturnRecordById(maPM: String?): ReturnRecord? {
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM PhieuTra WHERE MaPT = ?", arrayOf(maPM))
        var returnRecord: ReturnRecord? = null
        if (cursor.moveToFirst()) {
            returnRecord = cursor(cursor)
        }

        cursor.close()
        db.close()

        return returnRecord
    }
}