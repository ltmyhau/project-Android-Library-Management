package com.example.project_android_library_management.dao

import android.content.ContentValues
import android.database.Cursor
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.ReturnDetail

class ReturnDetailDao(private val databaseHelper: DatabaseHelper) {

    fun insert(returnDetail: ReturnDetail): Int {
        val db = databaseHelper.openDatabase()

        val contentValues = ContentValues().apply {
            put("MaPT", returnDetail.MaPT)
            put("MaSach", returnDetail.MaSach)
            put("SoLuong", returnDetail.SoLuong)
        }
        val result = db.insert("CTPhieuTra", null, contentValues)
        db.close()
        return if (result == -1L) 0 else 1
    }

    fun update(returnDetail: ReturnDetail): Int {
        val db = databaseHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put("MaPT", returnDetail.MaPT)
            put("MaSach", returnDetail.MaSach)
            put("SoLuong", returnDetail.SoLuong)
        }

        val whereClause = "MaPT = ? AND MaSach = ?"
        val whereArgs = arrayOf(returnDetail.MaPT, returnDetail.MaSach)

        val rowsAffected = db.update("CTPhieuTra", contentValues, whereClause, whereArgs)
        db.close()
        return rowsAffected
    }

    fun delete(borrowRecordId: String): Int {
        val db = databaseHelper.writableDatabase
        db.execSQL("PRAGMA foreign_keys = ON;")
        val rowsAffected = db.delete("CTPhieuTra", "MaPT = ?", arrayOf(borrowRecordId))
        db.close()
        return rowsAffected
    }

    private fun cursor(cursor: Cursor): ReturnDetail {
        val maPT = cursor.getString(cursor.getColumnIndexOrThrow("MaPT"))
        val maSach = cursor.getString(cursor.getColumnIndexOrThrow("MaSach"))
        val soLuong = cursor.getInt(cursor.getColumnIndexOrThrow("SoLuong"))

        return ReturnDetail(maPT, maSach, soLuong)
    }

    fun getReturnDetailById(maPT: String?): ArrayList<ReturnDetail> {
        val returnDetails = ArrayList<ReturnDetail>()
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM CTPhieuTra WHERE MaPT = ?", arrayOf(maPT))
        if (cursor.moveToFirst()) {
            do {
                returnDetails.add(cursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return returnDetails
    }
}