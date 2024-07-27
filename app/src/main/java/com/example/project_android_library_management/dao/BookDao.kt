package com.example.project_android_library_management.dao

import android.content.ContentValues
import android.database.Cursor
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.Book

class BookDao(private val databaseHelper: DatabaseHelper) {

    fun generateNewId(): String {
        var newId = "S00001"
        val db = databaseHelper.readableDatabase
        db.rawQuery("SELECT MAX(MaSach) FROM Sach", null).use { cursor ->
            if (cursor.moveToFirst()) {
                val maxCode = cursor.getString(0)
                if (!maxCode.isNullOrEmpty()) {
                    val currentNumber = maxCode.substring(1).toInt()
                    val newNumber = currentNumber + 1
                    newId = "S" + String.format("%05d", newNumber)
                }
            }
        }
        db.close()
        return newId
    }

    fun insert(book: Book): Int {
//        val db = databaseHelper.writableDatabase
        val db = databaseHelper.openDatabase()

        val contentValues = ContentValues().apply {
            put("MaSach", book.MaSach)
            put("ISBN", book.ISBN)
            put("TenSach", book.TenSach)
            put("TacGia", book.TacGia)
            put("MaNXB", book.MaNXB)
            put("NamXB", book.NamXB)
            put("SoTrang", book.SoTrang)
            put("SoLuongTon", book.SoLuongTon)
            put("GiaBan", book.GiaBan)
            put("MoTa", book.MoTa)
            put("HinhAnh", book.HinhAnh)
            put("MaTL", book.MaTL)
        }
        val result = db.insert("Sach", null, contentValues)
        db.close()
        return if (result == -1L) 0 else 1
    }

    fun update(book: Book): Int {
        val db = databaseHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put("ISBN", book.ISBN)
            put("TenSach", book.TenSach)
            put("TacGia", book.TacGia)
            put("MaNXB", book.MaNXB)
            put("NamXB", book.NamXB)
            put("SoTrang", book.SoTrang)
            put("SoLuongTon", book.SoLuongTon)
            put("GiaBan", book.GiaBan)
            put("MoTa", book.MoTa)
            put("HinhAnh", book.HinhAnh)
            put("MaTL", book.MaTL)
        }

        val rowsAffected = db.update("Sach", contentValues, "MaSach = ?", arrayOf(book.MaSach))
        db.close()
        return rowsAffected
    }

    fun delete(bookId: String): Int {
        val db = databaseHelper.writableDatabase
        db.execSQL("PRAGMA foreign_keys = ON;")
        val rowsAffected = db.delete("Sach", "MaSach = ?", arrayOf(bookId))
        db.close()
        return rowsAffected
    }

    private fun cursor(cursor: Cursor): Book {
        val maSach = cursor.getString(cursor.getColumnIndexOrThrow("MaSach"))
        val isbn = cursor.getString(cursor.getColumnIndexOrThrow("ISBN"))
        val tenSach = cursor.getString(cursor.getColumnIndexOrThrow("TenSach"))
        val tacGia = cursor.getString(cursor.getColumnIndexOrThrow("TacGia"))
        val maNXB = cursor.getString(cursor.getColumnIndexOrThrow("MaNXB"))
        val namXB = cursor.getInt(cursor.getColumnIndexOrThrow("NamXB"))
        val soTrang = cursor.getInt(cursor.getColumnIndexOrThrow("SoTrang"))
        val soLuongTon = cursor.getInt(cursor.getColumnIndexOrThrow("SoLuongTon"))
        val giaBan = cursor.getDouble(cursor.getColumnIndexOrThrow("GiaBan"))
        val moTa = cursor.getString(cursor.getColumnIndexOrThrow("MoTa"))
        val hinhAnh = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh"))
        val maTL = cursor.getString(cursor.getColumnIndexOrThrow("MaTL"))

        return Book(maSach, isbn, tenSach, tacGia, maNXB, namXB, soTrang, soLuongTon, giaBan, moTa, hinhAnh, maTL)
    }

    fun getAllBooks(): ArrayList<Book> {
        val books = ArrayList<Book>()
        val db = databaseHelper.openDatabase()

        val cursor: Cursor = db.rawQuery("SELECT * FROM Sach", null)
        if (cursor.moveToFirst()) {
            do {
                books.add(cursor(cursor))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return books
    }

    fun getBookById(bookId: String?): Book? {
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM Sach WHERE MaSach = ?", arrayOf(bookId))
        var book: Book? = null
        if (cursor.moveToFirst()) {
            book = cursor(cursor)
        }

        cursor.close()
        db.close()

        return book
    }

    fun getBookPrice(bookId: String?): Double {
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT GiaBan FROM Sach WHERE MaSach = ?", arrayOf(bookId))
        var price = 0.0
        if (cursor.moveToFirst()) {
            price = cursor.getDouble(cursor.getColumnIndexOrThrow("GiaBan"))
        }
        cursor.close()
        db.close()
        return price
    }
}
