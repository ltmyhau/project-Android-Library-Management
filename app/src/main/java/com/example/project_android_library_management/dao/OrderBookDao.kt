package com.example.project_android_library_management.dao

import android.content.ContentValues
import android.database.Cursor
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.BorrowRecord
import com.example.project_android_library_management.model.OrderBook

class OrderBookDao(private val databaseHelper: DatabaseHelper) {

    fun generateNewId(): String {
        var newId = "PD00001"
        val db = databaseHelper.readableDatabase
        db.rawQuery("SELECT MAX(MaPD) FROM PhieuDat", null).use { cursor ->
            if (cursor.moveToFirst()) {
                val maxCode = cursor.getString(0)
                if (!maxCode.isNullOrEmpty()) {
                    val currentNumber = maxCode.substring(2).toInt()
                    val newNumber = currentNumber + 1
                    newId = "PD" + String.format("%05d", newNumber)
                }
            }
        }
        db.close()
        return newId
    }

    fun insert(orderBook: OrderBook): Int {
        val db = databaseHelper.openDatabase()

        val contentValues = ContentValues().apply {
            put("MaPD", orderBook.MaPD)
            put("NgayDat", orderBook.NgayDat)
            put("GhiChu", orderBook.GhiChu)
            put("MaTT", orderBook.MaTT)
            put("MaNXB", orderBook.MaNXB)
        }
        val result = db.insert("PhieuDat", null, contentValues)
        db.close()
        return if (result == -1L) 0 else 1
    }

    fun update(orderBook: OrderBook): Int {
        val db = databaseHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put("NgayDat", orderBook.NgayDat)
            put("GhiChu", orderBook.GhiChu)
            put("MaTT", orderBook.MaTT)
            put("MaNXB", orderBook.MaNXB)
        }

        val rowsAffected = db.update("PhieuDat", contentValues, "MaPD = ?", arrayOf(orderBook.MaPD))
        db.close()
        return rowsAffected
    }

    fun delete(maPD: String): Int {
        val db = databaseHelper.writableDatabase
        db.execSQL("PRAGMA foreign_keys = ON;")
        db.delete("CTPhieuDat", "MaPD = ?", arrayOf(maPD))
        val rowsAffected = db.delete("PhieuDat", "MaPD = ?", arrayOf(maPD))
        db.close()
        return rowsAffected
    }

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