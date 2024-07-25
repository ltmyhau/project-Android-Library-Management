package com.example.project_android_library_management.dao

import android.database.Cursor
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.BorrowRecord

class BorrowRecordDao(private val databaseHelper: DatabaseHelper) {

    private fun cursor(cursor: Cursor): BorrowRecord {
        val maPM = cursor.getString(cursor.getColumnIndexOrThrow("MaPM"))
        val ngayMuon = cursor.getString(cursor.getColumnIndexOrThrow("NgayMuon"))
        val soNgayMuon = cursor.getInt(cursor.getColumnIndexOrThrow("SoNgayMuon"))
        val tienCoc = cursor.getDouble(cursor.getColumnIndexOrThrow("TienCoc"))
        val ghiChu = cursor.getString(cursor.getColumnIndexOrThrow("GhiChu"))
        val maDG = cursor.getString(cursor.getColumnIndexOrThrow("MaDG"))
        val maTT = cursor.getString(cursor.getColumnIndexOrThrow("MaTT"))

        return BorrowRecord(maPM, ngayMuon, soNgayMuon, tienCoc, ghiChu, maDG, maTT)
    }

    fun getAllBorrowRecord(): ArrayList<BorrowRecord> {
        val borrowRecords = ArrayList<BorrowRecord>()
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM PhieuMuon", null)
        if (cursor.moveToFirst()) {
            do {
                borrowRecords.add(cursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return borrowRecords
    }

    fun getBorrowRecordById(maPM: String?): BorrowRecord? {
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM PhieuMuon WHERE MaPM = ?", arrayOf(maPM))
        var borrowRecord: BorrowRecord? = null
        if (cursor.moveToFirst()) {
            borrowRecord = cursor(cursor)
        }

        cursor.close()
        db.close()

        return borrowRecord
    }
}