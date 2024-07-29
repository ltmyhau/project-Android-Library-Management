package com.example.project_android_library_management.dao

import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.ReturnRecord

class ReturnRecordDao(private val databaseHelper: DatabaseHelper) {

    fun generateNewId(): String {
        var newId = "PT00001"
        val db = databaseHelper.readableDatabase
        db.rawQuery("SELECT MAX(MaPT) FROM PhieuTra", null).use { cursor ->
            if (cursor.moveToFirst()) {
                val maxCode = cursor.getString(0)
                if (!maxCode.isNullOrEmpty()) {
                    val currentNumber = maxCode.substring(2).toInt()
                    val newNumber = currentNumber + 1
                    newId = "PT" + String.format("%05d", newNumber)
                }
            }
        }
        db.close()
        return newId
    }

    fun insert(returnRecord: ReturnRecord): Int {
        val db = databaseHelper.openDatabase()

        val contentValues = ContentValues().apply {
            put("MaPT", returnRecord.MaPT)
            put("NgayTra", returnRecord.NgayTra)
            put("TienPhat", returnRecord.TienPhat)
            put("MaPM", returnRecord.MaPM)
            put("MaTT", returnRecord.MaTT)
            put("MaDG", returnRecord.MaDG)
        }
        val result = db.insert("PhieuTra", null, contentValues)
        db.close()
        return if (result == -1L) 0 else 1
    }

    fun update(returnRecord: ReturnRecord): Int {
        val db = databaseHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put("NgayTra", returnRecord.NgayTra)
            put("TienPhat", returnRecord.TienPhat)
            put("MaPM", returnRecord.MaPM)
            put("MaTT", returnRecord.MaTT)
            put("MaDG", returnRecord.MaDG)
        }

        val rowsAffected =
            db.update("PhieuTra", contentValues, "MaPT = ?", arrayOf(returnRecord.MaPT))
        db.close()
        return rowsAffected
    }

    fun delete(returnId: String): Int {
        val db = databaseHelper.writableDatabase
        db.execSQL("PRAGMA foreign_keys = ON;")
        db.delete("CTPhieuTra", "MaPT = ?", arrayOf(returnId))
        val rowsAffected = db.delete("PhieuTra", "MaPT = ?", arrayOf(returnId))
        db.close()
        return rowsAffected
    }

    private fun cursor(cursor: Cursor): ReturnRecord {
        val maPT = cursor.getString(cursor.getColumnIndexOrThrow("MaPT"))
        val ngayTra = cursor.getString(cursor.getColumnIndexOrThrow("NgayTra"))
        val tienPhat = cursor.getDouble(cursor.getColumnIndexOrThrow("TienPhat"))
        val maPM = cursor.getString(cursor.getColumnIndexOrThrow("MaPM"))
        val maTT = cursor.getString(cursor.getColumnIndexOrThrow("MaTT"))
        val maDG = cursor.getString(cursor.getColumnIndexOrThrow("MaDG"))

        return ReturnRecord(maPT, ngayTra, tienPhat, maPM, maTT, maDG)
    }

    fun getAllReturnRecord(): ArrayList<ReturnRecord> {
        val returnRecord = ArrayList<ReturnRecord>()
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM PhieuTra", null)
        if (cursor.moveToFirst()) {
            do {
                returnRecord.add(cursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return returnRecord
    }

    fun getReturnRecordById(returnId: String?): ReturnRecord? {
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM PhieuTra WHERE MaPT = ?", arrayOf(returnId))
        var returnRecord: ReturnRecord? = null
        if (cursor.moveToFirst()) {
            returnRecord = cursor(cursor)
        }

        cursor.close()
        db.close()

        return returnRecord
    }

    fun getReturnRecordByBorrowId(borrowId: String?): ReturnRecord? {
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM PhieuTra WHERE MaPM = ?", arrayOf(borrowId))
        var returnRecord: ReturnRecord? = null
        if (cursor.moveToFirst()) {
            returnRecord = cursor(cursor)
        }

        cursor.close()
        db.close()

        return returnRecord
    }

    fun searchReturnRecord(query: String): ArrayList<ReturnRecord> {
        val returnRecord = ArrayList<ReturnRecord>()
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery(
            """
                SELECT * FROM PhieuTra
                WHERE LOWER(MaPT) LIKE ? OR
                      LOWER(TienPhat) LIKE ? OR
                      LOWER(MaPM) LIKE ? OR
                      LOWER(MaTT) LIKE ? OR
                      LOWER(MaDG) LIKE ?
            """.trimIndent(),
            arrayOf("%$query%", "%$query%", "%$query%", "%$query%", "%$query%")
        )
        if (cursor.moveToFirst()) {
            do {
                returnRecord.add(cursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return returnRecord
    }

    fun searchReturnByFilter(
        fromDay: String,
        toDay: String,
        reader: String,
        librarian: String
    ): ArrayList<ReturnRecord> {
        val returnRecord = ArrayList<ReturnRecord>()
        val db = databaseHelper.openDatabase()
        val query = StringBuilder(
            """
                SELECT pt.*, dg.HoTen AS TenDocGia, tt.HoTen AS TenThuThu 
                FROM PhieuTra pt
                    LEFT JOIN DocGia dg ON pt.MaDG = dg.MaDG 
                    LEFT JOIN ThuThu tt ON pt.MaTT = tt.MaTT
                WHERE 1=1
            """.trimIndent()
        )

        val args = ArrayList<String>()
        if (fromDay.isNotEmpty()) {
            query.append(" AND NgayTra >= ?")
            args.add(fromDay)
        }
        if (toDay.isNotEmpty()) {
            query.append(" AND NgayTra <= ?")
            args.add(toDay)
        }
        if (reader.isNotEmpty()) {
            query.append(" AND (pt.MaDG LIKE ? OR dg.HoTen LIKE ?)")
            args.add("%$reader%")
            args.add("%$reader%")
        }
        if (librarian.isNotEmpty()) {
            query.append(" AND (pt.MaTT LIKE ? OR tt.HoTen LIKE ?)")
            args.add("%$librarian%")
            args.add("%$librarian%")
        }

        val cursor: Cursor =
            db.rawQuery(query.toString(), args.toArray(arrayOfNulls<String>(args.size)))
        if (cursor.moveToFirst()) {
            do {
                returnRecord.add(cursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return returnRecord
    }
}