package com.example.project_android_library_management.dao

import android.content.ContentValues
import android.database.Cursor
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.Book

class BookDao(private val databaseHelper: DatabaseHelper) {

    fun insertBook(book: Book): Int {
        val db = databaseHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put("ISBN", book.ISBN)
            put("TenSach", book.TenSach)
            put("TacGia", book.TacGia)
            put("NXB", book.NXB)
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

    fun updateBook(book: Book): Int {
        val db = databaseHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put("TenSach", book.TenSach)
            put("TacGia", book.TacGia)
            put("NXB", book.NXB)
            put("NamXB", book.NamXB)
            put("SoTrang", book.SoTrang)
            put("SoLuongTon", book.SoLuongTon)
            put("GiaBan", book.GiaBan)
            put("MoTa", book.MoTa)
            put("HinhAnh", book.HinhAnh)
            put("MaTL", book.MaTL)
        }

        val rowsAffected = db.update("Sach", contentValues, "ISBN = ?", arrayOf(book.ISBN))
        db.close()
        return rowsAffected
    }

    private fun cursorToBook(cursor: Cursor): Book {
        val isbn = cursor.getString(cursor.getColumnIndexOrThrow("ISBN"))
        val tenSach = cursor.getString(cursor.getColumnIndexOrThrow("TenSach"))
        val tacGia = cursor.getString(cursor.getColumnIndexOrThrow("TacGia"))
        val nxb = cursor.getString(cursor.getColumnIndexOrThrow("NXB"))
        val namXB = cursor.getInt(cursor.getColumnIndexOrThrow("NamXB"))
        val soTrang = cursor.getInt(cursor.getColumnIndexOrThrow("SoTrang"))
        val soLuongTon = cursor.getInt(cursor.getColumnIndexOrThrow("SoLuongTon"))
        val giaBan = cursor.getDouble(cursor.getColumnIndexOrThrow("GiaBan"))
        val moTa = cursor.getString(cursor.getColumnIndexOrThrow("MoTa"))
        val hinhAnh = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh"))
        val maTL = cursor.getInt(cursor.getColumnIndexOrThrow("MaTL"))

        return Book(isbn, tenSach, tacGia, nxb, namXB, soTrang, soLuongTon, giaBan, moTa, hinhAnh, maTL)
    }

    fun getAllBooks(): ArrayList<Book> {
        val books = ArrayList<Book>()
        val db = databaseHelper.openDatabase()

        val cursor: Cursor = db.rawQuery("SELECT * FROM Sach", null)
        if (cursor.moveToFirst()) {
            do {
                books.add(cursorToBook(cursor))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return books
    }

    fun getBookByIsbn(isbn: String?): Book? {
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM Sach WHERE ISBN = ?", arrayOf(isbn))
        var book: Book? = null
        if (cursor.moveToFirst()) {
            book = cursorToBook(cursor)
        }

        cursor.close()
        db.close()

        return book
    }
}
