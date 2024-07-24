package com.example.project_android_library_management.dao

import android.database.Cursor
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.BookCategory

class BookCategoryDao(private val databaseHelper: DatabaseHelper) {
    fun getBookCategoryNameById(categoryId: Int): String? {
        val db = databaseHelper.openDatabase()
        var categoryName: String? = null

        val cursor: Cursor = db.rawQuery("SELECT TenLoai FROM TheLoai WHERE MaLoai = ?", arrayOf(categoryId.toString()))
        if (cursor.moveToFirst()) {
            categoryName = cursor.getString(cursor.getColumnIndexOrThrow("TenLoai"))
        }
        cursor.close()
        db.close()

        return categoryName
    }

    fun getAllBookCategories(): List<BookCategory> {
        val categories = mutableListOf<BookCategory>()
        val db = databaseHelper.openDatabase()
        val cursor = db.rawQuery("SELECT * FROM TheLoai", null)
        if (cursor.moveToFirst()) {
            do {
                val maLoai = cursor.getInt(cursor.getColumnIndexOrThrow("MaLoai"))
                val tenLoai = cursor.getString(cursor.getColumnIndexOrThrow("TenLoai"))
                categories.add(BookCategory(maLoai, tenLoai))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return categories
    }
}