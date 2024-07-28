package com.example.project_android_library_management.dao

import android.database.Cursor
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.Librarian
import com.example.project_android_library_management.model.Reader

class LibrarianDao(private val databaseHelper: DatabaseHelper) {

    private fun cursor(cursor: Cursor): Librarian {
        val maTT = cursor.getString(cursor.getColumnIndexOrThrow("MaTT"))
        val hoTen = cursor.getString(cursor.getColumnIndexOrThrow("HoTen"))
        val ngaySinh = cursor.getString(cursor.getColumnIndexOrThrow("NgaySinh"))
        val gioiTinh = cursor.getString(cursor.getColumnIndexOrThrow("GioiTinh"))
        val dienThoai = cursor.getString(cursor.getColumnIndexOrThrow("DienThoai"))
        val diaChi = cursor.getString(cursor.getColumnIndexOrThrow("DiaChi"))
        val hinhAnh = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh"))

        return Librarian(maTT, hoTen, ngaySinh, gioiTinh, dienThoai, diaChi, hinhAnh)
    }

    fun getAllLibrarian(): ArrayList<Librarian> {
        val librarians = ArrayList<Librarian>()
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM ThuThu", null)
        if (cursor.moveToFirst()) {
            do {
                librarians.add(cursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return librarians
    }

    fun getLibrarianById(maTT: String?): Librarian? {
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM ThuThu WHERE MaTT = ?", arrayOf(maTT))
        var librarian: Librarian? = null
        if (cursor.moveToFirst()) {
            librarian = cursor(cursor)
        }

        cursor.close()
        db.close()

        return librarian
    }

    fun searchLibrarian(query: String): List<Librarian> {
        val librarians = ArrayList<Librarian>()
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery(
            """
                SELECT * FROM ThuThu
                WHERE LOWER(MaTT) LIKE ? OR
                      LOWER(HoTen) LIKE ? OR
                      LOWER(DienThoai) LIKE ?
            """.trimIndent(),
            arrayOf("%$query%", "%$query%", "%$query%")
        )
        if (cursor.moveToFirst()) {
            do {
                librarians.add(cursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return librarians
    }
}