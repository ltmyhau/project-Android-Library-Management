package com.example.project_android_library_management.dao

import android.database.Cursor
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.OrderBook

class OrderBookDao(private val databaseHelper: DatabaseHelper) {

    private fun cursor(cursor: Cursor): OrderBook {
        val maPD = cursor.getString(cursor.getColumnIndexOrThrow("MaPD"))
        val ngayDat = cursor.getString(cursor.getColumnIndexOrThrow("NgayDat"))
        val ghiChu = cursor.getString(cursor.getColumnIndexOrThrow("GhiChu"))
        val maTT = cursor.getString(cursor.getColumnIndexOrThrow("MaTT"))
        val maNXB = cursor.getString(cursor.getColumnIndexOrThrow("MaNXB"))

        return OrderBook(maPD, ngayDat, ghiChu, maTT, maNXB)
    }

    fun getAllOrderBook(): ArrayList<OrderBook> {
        val orderBooks = ArrayList<OrderBook>()
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM PhieuDat", null)
        if (cursor.moveToFirst()) {
            do {
                orderBooks.add(cursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return orderBooks
    }

    fun getOrderBookById(maPD: String?): OrderBook? {
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM PhieuDat WHERE MaPD = ?", arrayOf(maPD))
        var orderBook: OrderBook? = null
        if (cursor.moveToFirst()) {
            orderBook = cursor(cursor)
        }

        cursor.close()
        db.close()

        return orderBook
    }
}