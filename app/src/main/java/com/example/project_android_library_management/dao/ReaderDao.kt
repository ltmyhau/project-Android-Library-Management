package com.example.project_android_library_management.dao

import android.content.ContentValues
import android.database.Cursor
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.Reader

class ReaderDao(private val databaseHelper: DatabaseHelper) {

    fun generateNewId(): String {
        var newId = "DG00001"
        val db = databaseHelper.readableDatabase
        db.rawQuery("SELECT MAX(MaDG) FROM DocGia", null).use { cursor ->
            if (cursor.moveToFirst()) {
                val maxCode = cursor.getString(0)
                if (!maxCode.isNullOrEmpty()) {
                    val currentNumber = maxCode.substring(2).toInt()
                    val newNumber = currentNumber + 1
                    newId = "DG" + String.format("%05d", newNumber)
                }
            }
        }
        db.close()
        return newId
    }

    fun insert(reader: Reader): Int {
//        val db = databaseHelper.writableDatabase
        val db = databaseHelper.openDatabase()

        val contentValues = ContentValues().apply {
            put("MaDG", generateNewId())
            put("HoTen", reader.HoTen)
            put("NgaySinh", reader.NgaySinh)
            put("GioiTinh", reader.GioiTinh)
            put("DienThoai", reader.DienThoai)
            put("Email", reader.Email)
            put("DiaChi", reader.DiaChi)
            put("HinhAnh", reader.HinhAnh)
            put("NgayLamThe", reader.NgayLamThe)
        }
        val result = db.insert("DocGia", null, contentValues)
        db.close()
        return if (result == -1L) 0 else 1
    }

    fun update(reader: Reader): Int {
        val db = databaseHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put("HoTen", reader.HoTen)
            put("NgaySinh", reader.NgaySinh)
            put("GioiTinh", reader.GioiTinh)
            put("DienThoai", reader.DienThoai)
            put("Email", reader.Email)
            put("DiaChi", reader.DiaChi)
            put("HinhAnh", reader.HinhAnh)
            put("NgayLamThe", reader.NgayLamThe)
        }

        val rowsAffected = db.update("DocGia", contentValues, "MaDG = ?", arrayOf(reader.MaDG))
        db.close()
        return rowsAffected
    }

    fun delete(maDG: String): Int {
        val db = databaseHelper.writableDatabase
        val rowsAffected = db.delete("DocGia", "MaDG = ?", arrayOf(maDG))
        db.close()
        return rowsAffected
    }

    private fun cursor(cursor: Cursor): Reader {
        val maDG = cursor.getString(cursor.getColumnIndexOrThrow("MaDG"))
        val hoTen = cursor.getString(cursor.getColumnIndexOrThrow("HoTen"))
        val ngaySinh = cursor.getString(cursor.getColumnIndexOrThrow("NgaySinh"))
        val gioiTinh = cursor.getString(cursor.getColumnIndexOrThrow("GioiTinh"))
        val dienThoai = cursor.getString(cursor.getColumnIndexOrThrow("DienThoai"))
        val email = cursor.getString(cursor.getColumnIndexOrThrow("Email"))
        val diaChi = cursor.getString(cursor.getColumnIndexOrThrow("DiaChi"))
        val hinhAnh = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh"))
        val ngayLamThe = cursor.getString(cursor.getColumnIndexOrThrow("NgayLamThe"))

        return Reader(maDG, hoTen, ngaySinh, gioiTinh, dienThoai, email, diaChi, hinhAnh, ngayLamThe)
    }

    fun getAllReaders(): ArrayList<Reader> {
        val readers = ArrayList<Reader>()
        val db = databaseHelper.openDatabase()

        val cursor: Cursor = db.rawQuery("SELECT * FROM DocGia", null)
        if (cursor.moveToFirst()) {
            do {
                readers.add(cursor(cursor))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return readers
    }

    fun getReaderById(maDG: String?): Reader? {
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM DocGia WHERE MaDG = ?", arrayOf(maDG))
        var reader: Reader? = null
        if (cursor.moveToFirst()) {
            reader = cursor(cursor)
        }

        cursor.close()
        db.close()

        return reader
    }
}