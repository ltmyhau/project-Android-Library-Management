package com.example.project_android_library_management.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.Librarian
import com.example.project_android_library_management.model.Reader

class LibrarianDao(private val databaseHelper: DatabaseHelper) {

    fun generateNewId(): String {
        var newId = "TT00001"
        val db = databaseHelper.readableDatabase
        db.rawQuery("SELECT MAX(MaTT) FROM ThuThu", null).use { cursor ->
            if (cursor.moveToFirst()) {
                val maxCode = cursor.getString(0)
                if (!maxCode.isNullOrEmpty()) {
                    val currentNumber = maxCode.substring(2).toInt()
                    val newNumber = currentNumber + 1
                    newId = "TT" + String.format("%05d", newNumber)
                }
            }
        }
        db.close()
        return newId
    }

    fun insert(librarian: Librarian): Int {
        val db = databaseHelper.openDatabase()

        val contentValues = ContentValues().apply {
            put("MaTT", librarian.MaTT)
            put("HoTen", librarian.HoTen)
            put("NgaySinh", librarian.NgaySinh)
            put("GioiTinh", librarian.GioiTinh)
            put("DienThoai", librarian.DienThoai)
            put("Email", librarian.Email)
            put("DiaChi", librarian.DiaChi)
            put("HinhAnh", librarian.HinhAnh)
        }
        val result = db.insert("ThuThu", null, contentValues)
        db.close()
        return if (result == -1L) 0 else 1
    }

    fun update(librarian: Librarian): Int {
        val db = databaseHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put("HoTen", librarian.HoTen)
            put("NgaySinh", librarian.NgaySinh)
            put("GioiTinh", librarian.GioiTinh)
            put("DienThoai", librarian.DienThoai)
            put("Email", librarian.Email)
            put("DiaChi", librarian.DiaChi)
            put("HinhAnh", librarian.HinhAnh)
        }

        val rowsAffected = db.update("ThuThu", contentValues, "MaTT = ?", arrayOf(librarian.MaTT))
        db.close()
        return rowsAffected
    }

    fun delete(librarianId: String): Int {
        val db = databaseHelper.writableDatabase
        db.execSQL("PRAGMA foreign_keys = ON;")
        return try {
            val rowsAffected = db.delete("ThuThu", "MaTT = ?", arrayOf(librarianId))
            rowsAffected
        } catch (e: SQLiteConstraintException) {
            throw e
        } finally {
            db.close()
        }
    }

    private fun cursor(cursor: Cursor): Librarian {
        val maTT = cursor.getString(cursor.getColumnIndexOrThrow("MaTT"))
        val hoTen = cursor.getString(cursor.getColumnIndexOrThrow("HoTen"))
        val ngaySinh = cursor.getString(cursor.getColumnIndexOrThrow("NgaySinh"))
        val gioiTinh = cursor.getString(cursor.getColumnIndexOrThrow("GioiTinh"))
        val dienThoai = cursor.getString(cursor.getColumnIndexOrThrow("DienThoai"))
        val email = cursor.getString(cursor.getColumnIndexOrThrow("Email"))
        val diaChi = cursor.getString(cursor.getColumnIndexOrThrow("DiaChi"))
        val hinhAnh = cursor.getBlob(cursor.getColumnIndexOrThrow("HinhAnh"))

        return Librarian(maTT, hoTen, ngaySinh, gioiTinh, dienThoai, email, diaChi, hinhAnh)
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

    fun searchLibrarianByFilter(gender: List<String>, fromDay: String, toDay: String): ArrayList<Librarian> {
        val librarians = ArrayList<Librarian>()
        val db = databaseHelper.openDatabase()
        val query = StringBuilder("SELECT * FROM ThuThu WHERE 1=1")

        val args = ArrayList<String>()
        if (gender.isNotEmpty()) {
            query.append(" AND GioiTinh IN (${gender.joinToString { "?" }})")
            args.addAll(gender)
        }
        if (fromDay.isNotEmpty()) {
            query.append(" AND NgaySinh >= ?")
            args.add(fromDay)
        }
        if (toDay.isNotEmpty()) {
            query.append(" AND NgaySinh <= ?")
            args.add(toDay)
        }

        val cursor: Cursor = db.rawQuery(query.toString(), args.toArray(arrayOfNulls<String>(args.size)))

        if (cursor.moveToFirst()) {
            do {
                librarians.add(cursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return librarians
    }
}