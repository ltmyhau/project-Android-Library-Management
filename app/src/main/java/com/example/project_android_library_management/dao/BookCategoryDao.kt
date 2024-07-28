package com.example.project_android_library_management.dao

import android.database.Cursor
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.Book
import com.example.project_android_library_management.model.BookCategory

class BookCategoryDao(private val databaseHelper: DatabaseHelper) {
    fun getBookCategoryNameById(categoryId: String): String? {
        val db = databaseHelper.openDatabase()
        var categoryName: String? = null

        val cursor: Cursor =
            db.rawQuery("SELECT TenLoai FROM TheLoai WHERE MaLoai = ?", arrayOf(categoryId))
        if (cursor.moveToFirst()) {
            categoryName = cursor.getString(cursor.getColumnIndexOrThrow("TenLoai"))
        }
        cursor.close()
        db.close()

        return categoryName
    }

    fun getAllBookCategories(): ArrayList<BookCategory> {
        val categories = ArrayList<BookCategory>()
        val db = databaseHelper.openDatabase()
        val cursor = db.rawQuery("SELECT * FROM TheLoai", null)
        if (cursor.moveToFirst()) {
            do {
                val maLoai = cursor.getString(cursor.getColumnIndexOrThrow("MaLoai"))
                val tenLoai = cursor.getString(cursor.getColumnIndexOrThrow("TenLoai"))
                categories.add(BookCategory(maLoai, tenLoai))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return categories
    }

    fun searchBookCategories(query: String): ArrayList<BookCategory> {
        val categories = ArrayList<BookCategory>()
        val db = databaseHelper.openDatabase()

        val cursor: Cursor = db.rawQuery(
            """
                SELECT * FROM TheLoai
                WHERE LOWER(MaLoai) LIKE ? OR
                      LOWER(TenLoai) LIKE ?
            """.trimIndent(),
            arrayOf("%$query%", "%$query%")
        )

        if (cursor.moveToFirst()) {
            do {
                val maLoai = cursor.getString(cursor.getColumnIndexOrThrow("MaLoai"))
                val tenLoai = cursor.getString(cursor.getColumnIndexOrThrow("TenLoai"))
                categories.add(BookCategory(maLoai, tenLoai))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return categories
    }
}