package com.example.project_android_library_management.dao

import android.database.Cursor
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.Book

class BookDao(private val databaseHelper: DatabaseHelper) {

    fun getAllBooks(): ArrayList<Book> {
        val books = ArrayList<Book>()
        val db = databaseHelper.openDatabase()

        val cursor: Cursor = db.rawQuery("SELECT * FROM Sach", null)
        if (cursor.moveToFirst()) {
            do {
                val book = Book(
                    cursor.getString(cursor.getColumnIndexOrThrow("ISBN")),
                    cursor.getString(cursor.getColumnIndexOrThrow("TenSach")),
                    cursor.getString(cursor.getColumnIndexOrThrow("TacGia")),
                    cursor.getString(cursor.getColumnIndexOrThrow("NXB")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("NamXB")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("SoTrang")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("SoLuongTon")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("GiaBan")),
                    cursor.getString(cursor.getColumnIndexOrThrow("MoTa")),
                    cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("MaTL"))
                )
                books.add(book)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return books
    }
}
