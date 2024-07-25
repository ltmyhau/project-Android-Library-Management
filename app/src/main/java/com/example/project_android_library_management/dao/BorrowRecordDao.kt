package com.example.project_android_library_management.dao

import android.content.ContentValues
import android.database.Cursor
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.BorrowRecord

class BorrowRecordDao(private val databaseHelper: DatabaseHelper) {

    fun generateNewId(): String {
        var newId = "PM00001"
        val db = databaseHelper.readableDatabase
        db.rawQuery("SELECT MAX(MaPM) FROM PhieuMuon", null).use { cursor ->
            if (cursor.moveToFirst()) {
                val maxCode = cursor.getString(0)
                if (!maxCode.isNullOrEmpty()) {
                    val currentNumber = maxCode.substring(2).toInt()
                    val newNumber = currentNumber + 1
                    newId = "PM" + String.format("%05d", newNumber)
                }
            }
        }
        db.close()
        return newId
    }

    fun insert(borrowRecord: BorrowRecord): Int {
        val db = databaseHelper.openDatabase()

        val contentValues = ContentValues().apply {
            put("MaPM", generateNewId())
            put("NgayMuon", borrowRecord.NgayMuon)
            put("SoNgayMuon", borrowRecord.SoNgayMuon)
            put("TienCoc", borrowRecord.TienCoc)
            put("GhiChu", borrowRecord.GhiChu)
            put("MaDG", borrowRecord.MaDG)
            put("MaTT", borrowRecord.MaTT)
        }
        val result = db.insert("PhieuMuon", null, contentValues)
        db.close()
        return if (result == -1L) 0 else 1
    }

    fun update(borrowRecord: BorrowRecord): Int {
        val db = databaseHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put("NgayMuon", borrowRecord.NgayMuon)
            put("SoNgayMuon", borrowRecord.SoNgayMuon)
            put("TienCoc", borrowRecord.TienCoc)
            put("GhiChu", borrowRecord.GhiChu)
            put("MaDG", borrowRecord.MaDG)
            put("MaTT", borrowRecord.MaTT)
        }

        val rowsAffected = db.update("PhieuMuon", contentValues, "MaPM = ?", arrayOf(borrowRecord.MaPM))
        db.close()
        return rowsAffected
    }

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