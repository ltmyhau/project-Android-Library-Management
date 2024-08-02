package com.example.project_android_library_management.dao

import android.database.Cursor
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.Publisher

class PublisherDao(private val databaseHelper: DatabaseHelper) {

    private fun cursor(cursor: Cursor): Publisher {
        val maNXB = cursor.getString(cursor.getColumnIndexOrThrow("MaNXB"))
        val tenNXB = cursor.getString(cursor.getColumnIndexOrThrow("TenNXB"))
        val diaChi = cursor.getString(cursor.getColumnIndexOrThrow("DiaChi"))
        val email = cursor.getString(cursor.getColumnIndexOrThrow("Email"))
        val dienThoai = cursor.getString(cursor.getColumnIndexOrThrow("DienThoai"))
        val hinhAnh = cursor.getBlob(cursor.getColumnIndexOrThrow("HinhAnh"))

        return Publisher(maNXB, tenNXB, diaChi, email, dienThoai, hinhAnh)
    }

    fun getAllPublisher(): ArrayList<Publisher> {
        val publishers = ArrayList<Publisher>()
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM NhaXuatBan", null)
        if (cursor.moveToFirst()) {
            do {
                publishers.add(cursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return publishers
    }

    fun getPublisherById(maNXB: String?): Publisher? {
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM NhaXuatBan WHERE MaNXB = ?", arrayOf(maNXB))
        var publisher: Publisher? = null
        if (cursor.moveToFirst()) {
            publisher = cursor(cursor)
        }

        cursor.close()
        db.close()

        return publisher
    }

    fun searchPublisher(query: String): List<Publisher> {
        val publishers = ArrayList<Publisher>()
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery(
            """
                SELECT * FROM NhaXuatBan
                WHERE LOWER(MaNXB) LIKE ? OR
                      LOWER(TenNXB) LIKE ? OR
                      LOWER(Email) LIKE ? OR
                      LOWER(DienThoai) LIKE ?
            """.trimIndent(),
            arrayOf("%$query%", "%$query%", "%$query%", "%$query%")
        )
        if (cursor.moveToFirst()) {
            do {
                publishers.add(cursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return publishers
    }
}