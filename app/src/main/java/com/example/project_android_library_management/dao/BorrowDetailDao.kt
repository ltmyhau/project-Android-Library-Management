package com.example.project_android_library_management.dao

import android.content.ContentValues
import android.database.Cursor
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.BorrowDetail

class BorrowDetailDao(private val databaseHelper: DatabaseHelper) {

    fun insert(borrowDetail: BorrowDetail): Int {
        val db = databaseHelper.openDatabase()

        val contentValues = ContentValues().apply {
            put("MaPM", borrowDetail.MaPM)
            put("MaSach", borrowDetail.MaSach)
            put("SoLuong", borrowDetail.SoLuong)
        }
        val result = db.insert("CTPhieuMuon", null, contentValues)
        db.close()
        return if (result == -1L) 0 else 1
    }

    fun update(borrowDetail: BorrowDetail): Int {
        val db = databaseHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put("MaPM", borrowDetail.MaPM)
            put("MaSach", borrowDetail.MaSach)
            put("SoLuong", borrowDetail.SoLuong)
        }

        val whereClause = "MaPM = ? AND MaSach = ?"
        val whereArgs = arrayOf(borrowDetail.MaPM, borrowDetail.MaSach)

        val rowsAffected = db.update("CTPhieuMuon", contentValues, whereClause, whereArgs)
        db.close()
        return rowsAffected
    }

    fun delete(borrowRecordId: String): Int {
        val db = databaseHelper.writableDatabase
        db.execSQL("PRAGMA foreign_keys = ON;")
        val rowsAffected = db.delete("CTPhieuMuon", "MaPM = ?", arrayOf(borrowRecordId))
        db.close()
        return rowsAffected
    }

    private fun cursor(cursor: Cursor): BorrowDetail {
        val maPM = cursor.getString(cursor.getColumnIndexOrThrow("MaPM"))
        val maSach = cursor.getString(cursor.getColumnIndexOrThrow("MaSach"))
        val soLuong = cursor.getInt(cursor.getColumnIndexOrThrow("SoLuong"))

        return BorrowDetail(maPM, maSach, soLuong)
    }

    fun getBorrowDetailById(maPM: String?): ArrayList<BorrowDetail> {
        val borrowDetails = ArrayList<BorrowDetail>()
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM CTPhieuMuon WHERE MaPM = ?", arrayOf(maPM))
        if (cursor.moveToFirst()) {
            do {
                borrowDetails.add(cursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return borrowDetails
    }
}