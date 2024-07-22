package com.example.project_android_library_management.dao

import android.database.Cursor
import com.example.project_android_library_management.DatabaseHelper

class CategoryDao(private val databaseHelper: DatabaseHelper) {
    fun getCategoryNameById(categoryId: Int): String? {
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
}