package com.example.project_android_library_management.dao

import android.content.ContentValues
import android.database.Cursor
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.BorrowDetail
import com.example.project_android_library_management.model.OrderDetail

class OrderDetailDao(private val databaseHelper: DatabaseHelper) {

    fun insert(orderDetail: OrderDetail): Int {
        val db = databaseHelper.openDatabase()

        val contentValues = ContentValues().apply {
            put("MaPD", orderDetail.MaPD)
            put("MaSach", orderDetail.MaSach)
            put("SoLuong", orderDetail.SoLuong)
        }
        val result = db.insert("CTPhieuDat", null, contentValues)
        db.close()
        return if (result == -1L) 0 else 1
    }

    fun update(orderDetail: OrderDetail): Int {
        val db = databaseHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put("MaPD", orderDetail.MaPD)
            put("MaSach", orderDetail.MaSach)
            put("SoLuong", orderDetail.SoLuong)
        }

        val whereClause = "MaPD = ? AND MaSach = ?"
        val whereArgs = arrayOf(orderDetail.MaPD, orderDetail.MaSach)

        val rowsAffected = db.update("CTPhieuDat", contentValues, whereClause, whereArgs)
        db.close()
        return rowsAffected
    }

    fun delete(orderDetailId: String): Int {
        val db = databaseHelper.writableDatabase
        db.execSQL("PRAGMA foreign_keys = ON;")
        val rowsAffected = db.delete("CTPhieuDat", "MaPD = ?", arrayOf(orderDetailId))
        db.close()
        return rowsAffected
    }

    private fun cursor(cursor: Cursor): OrderDetail {
        val maPD = cursor.getString(cursor.getColumnIndexOrThrow("MaPD"))
        val maSach = cursor.getString(cursor.getColumnIndexOrThrow("MaSach"))
        val soLuong = cursor.getInt(cursor.getColumnIndexOrThrow("SoLuong"))

        return OrderDetail(maPD, maSach, soLuong)
    }

    fun getOrderDetailsById(maPD: String?): ArrayList<OrderDetail> {
        val orderDetails = ArrayList<OrderDetail>()
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM CTPhieuDat WHERE MaPD = ?", arrayOf(maPD))
        if (cursor.moveToFirst()) {
            do {
                orderDetails.add(cursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return orderDetails
    }
}