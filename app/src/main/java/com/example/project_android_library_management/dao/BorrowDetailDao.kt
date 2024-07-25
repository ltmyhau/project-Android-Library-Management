package com.example.project_android_library_management.dao

import android.database.Cursor
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.BorrowDetail

class BorrowDetailDao(private val databaseHelper: DatabaseHelper) {

    private fun cursor(cursor: Cursor): BorrowDetail {
        val maPM = cursor.getString(cursor.getColumnIndexOrThrow("MaPM"))
        val maSach = cursor.getString(cursor.getColumnIndexOrThrow("MaSach"))
        val soLuong = cursor.getInt(cursor.getColumnIndexOrThrow("SoLuong"))
        val ghiChu = cursor.getString(cursor.getColumnIndexOrThrow("GhiChu"))

        return BorrowDetail(maPM, maSach, soLuong, ghiChu)
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