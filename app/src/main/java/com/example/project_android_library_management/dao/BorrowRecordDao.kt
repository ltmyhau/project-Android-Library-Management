package com.example.project_android_library_management.dao

import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.BorrowRecord
import com.example.project_android_library_management.model.Reader

class BorrowRecordDao(private val databaseHelper: DatabaseHelper) {

    fun generateNewId(): String {
        var newId = "PM00001"
        val db = databaseHelper.readableDatabase
        db.rawQuery("SELECT MAX(MaPM) FROM PhieuMuon", null).use { cursor ->
            if (cursor.moveToFirst()) {
                val maxCode = cursor.getString(0)
                if (!maxCode.isNullOrEmpty()) {
                    val currentNumber = maxCode.substring(2).toInt()
                    val newNumber = currentNumber + 1
                    newId = "PM" + String.format("%05d", newNumber)
                }
            }
        }
        db.close()
        return newId
    }

    fun insert(borrowRecord: BorrowRecord): Int {
        val db = databaseHelper.openDatabase()

        val contentValues = ContentValues().apply {
            put("MaPM", borrowRecord.MaPM)
            put("NgayMuon", borrowRecord.NgayMuon)
            put("SoNgayMuon", borrowRecord.SoNgayMuon)
            put("TienCoc", borrowRecord.TienCoc)
            put("GhiChu", borrowRecord.GhiChu)
            put("MaDG", borrowRecord.MaDG)
            put("MaTT", borrowRecord.MaTT)
        }
        val result = db.insert("PhieuMuon", null, contentValues)
        db.close()
        return if (result == -1L) 0 else 1
    }

    fun update(borrowRecord: BorrowRecord): Int {
        val db = databaseHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put("NgayMuon", borrowRecord.NgayMuon)
            put("SoNgayMuon", borrowRecord.SoNgayMuon)
            put("TienCoc", borrowRecord.TienCoc)
            put("GhiChu", borrowRecord.GhiChu)
            put("MaDG", borrowRecord.MaDG)
            put("MaTT", borrowRecord.MaTT)
        }

        val rowsAffected =
            db.update("PhieuMuon", contentValues, "MaPM = ?", arrayOf(borrowRecord.MaPM))
        db.close()
        return rowsAffected
    }

    fun delete(maPM: String): Int {
        val db = databaseHelper.writableDatabase
        db.execSQL("PRAGMA foreign_keys = ON;")
        db.delete("CTPhieuMuon", "MaPM = ?", arrayOf(maPM))
        val rowsAffected = db.delete("PhieuMuon", "MaPM = ?", arrayOf(maPM))
        db.close()
        return rowsAffected
    }

    private fun cursor(cursor: Cursor): BorrowRecord {
        val maPM = cursor.getString(cursor.getColumnIndexOrThrow("MaPM"))
        val ngayMuon = cursor.getString(cursor.getColumnIndexOrThrow("NgayMuon"))
        val soNgayMuon = cursor.getInt(cursor.getColumnIndexOrThrow("SoNgayMuon"))
        val tienCoc = cursor.getDouble(cursor.getColumnIndexOrThrow("TienCoc"))
        val ghiChu = cursor.getString(cursor.getColumnIndexOrThrow("GhiChu"))
        val maDG = cursor.getString(cursor.getColumnIndexOrThrow("MaDG"))
        val maTT = cursor.getString(cursor.getColumnIndexOrThrow("MaTT"))

        return BorrowRecord(maPM, ngayMuon, soNgayMuon, tienCoc, ghiChu, maDG, maTT)
    }

    fun getAllBorrowRecord(): ArrayList<BorrowRecord> {
        val borrowRecords = ArrayList<BorrowRecord>()
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM PhieuMuon", null)
        if (cursor.moveToFirst()) {
            do {
                borrowRecords.add(cursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return borrowRecords
    }

    fun getBorrowRecordById(maPM: String?): BorrowRecord? {
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery("SELECT * FROM PhieuMuon WHERE MaPM = ?", arrayOf(maPM))
        var borrowRecord: BorrowRecord? = null
        if (cursor.moveToFirst()) {
            borrowRecord = cursor(cursor)
        }

        cursor.close()
        db.close()

        return borrowRecord
    }

    fun getStatusBorrowRecordById(maPM: String?): String? {
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery(
            """
                SELECT pm.MaPM,
                    CASE
                        WHEN pt.MaPT IS NOT NULL THEN 'Đã trả'
                        WHEN DATE('now') > DATE(pm.NgayMuon, '+' || pm.SoNgayMuon || ' days') THEN 'Quá hạn'
                        ELSE 'Chưa trả'
                    END AS TrangThai
                FROM PhieuMuon pm
                    LEFT JOIN PhieuTra pt ON pm.MaPM = pt.MaPM
                WHERE pm.MaPM = ?
            """.trimIndent(), arrayOf(maPM)
        )
        var status: String? = null

        if (cursor.moveToFirst()) {
            status = cursor.getString(cursor.getColumnIndexOrThrow("TrangThai"))
        }

        cursor.close()
        db.close()

        return status
    }

    fun getReturnedBorrowRecordsByReaderId(readerId: String): ArrayList<BorrowRecord>{
        val borrowRecords = ArrayList<BorrowRecord>()
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery(
            """
                SELECT pm.*
                FROM PhieuMuon pm
                    JOIN PhieuTra pt ON pm.MaPM = pt.MaPM
                WHERE pm.MaDG = ?
            """.trimIndent(), arrayOf(readerId)
        )
        if (cursor.moveToFirst()) {
            do {
                borrowRecords.add(cursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return borrowRecords
    }

    fun getPendingOrOverdueBorrowRecordsByReaderId(readerId: String): ArrayList<BorrowRecord> {
        val borrowRecords = ArrayList<BorrowRecord>()
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery(
            """
                SELECT pm.*
                FROM PhieuMuon pm
                    LEFT JOIN PhieuTra pt ON pm.MaPM = pt.MaPM
                WHERE pm.MaDG = ? AND (pt.MaPT IS NULL OR DATE('now') > DATE(pm.NgayMuon, '+' || pm.SoNgayMuon || ' days'))
        """.trimIndent(), arrayOf(readerId)
        )
        if (cursor.moveToFirst()) {
            do {
                borrowRecords.add(cursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return borrowRecords
    }

    fun searchBorrowRecord(query: String): ArrayList<BorrowRecord> {
        val borrowRecords = ArrayList<BorrowRecord>()
        val db = databaseHelper.openDatabase()
        val cursor: Cursor = db.rawQuery(
            """
                SELECT * FROM PhieuMuon
                WHERE LOWER(MaPM) LIKE ? OR
                      LOWER(SoNgayMuon) LIKE ? OR
                      LOWER(TienCoc) LIKE ? OR
                      LOWER(MaDG) LIKE ? OR
                      LOWER(MaTT) LIKE ?
            """.trimIndent(),
            arrayOf("%$query%", "%$query%", "%$query%", "%$query%", "%$query%")
        )
        if (cursor.moveToFirst()) {
            do {
                borrowRecords.add(cursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return borrowRecords
    }

    fun searchBorrowByFilter(
        status: List<String>,
        fromDay: String,
        toDay: String,
        reader: String,
        librarian: String
    ): ArrayList<BorrowRecord> {
        val borrowRecords = ArrayList<BorrowRecord>()
        val db = databaseHelper.openDatabase()

        val query = StringBuilder(
            """
                SELECT pm.*, dg.HoTen AS TenDocGia, tt.HoTen AS TenThuThu,
                    CASE
                        WHEN pt.MaPT IS NOT NULL THEN 'Đã trả'
                        WHEN DATE('now') > DATE(pm.NgayMuon, '+' || pm.SoNgayMuon || ' days') THEN 'Quá hạn'
                        ELSE 'Chưa trả'
                    END AS TrangThai
                FROM PhieuMuon pm
                    LEFT JOIN PhieuTra pt ON pm.MaPM = pt.MaPM
                    LEFT JOIN DocGia dg ON pm.MaDG = dg.MaDG
                    LEFT JOIN ThuThu tt ON pm.MaTT = tt.MaTT
                WHERE 1=1
            """.trimIndent()
        )

        val args = ArrayList<String>()
        if (status.isNotEmpty()) {
            query.append(" AND (CASE " +
                    "WHEN pt.MaPT IS NOT NULL THEN 'Đã trả' " +
                    "WHEN DATE('now') > DATE(pm.NgayMuon, '+' || pm.SoNgayMuon || ' days') THEN 'Quá hạn' " +
                    "ELSE 'Chưa trả' " +
                    "END) IN (${status.joinToString { "?" }})")
            args.addAll(status)
        }
        if (fromDay.isNotEmpty()) {
            query.append(" AND NgayMuon >= ?")
            args.add(fromDay)
        }
        if (toDay.isNotEmpty()) {
            query.append(" AND NgayMuon <= ?")
            args.add(toDay)
        }
        if (reader.isNotEmpty()) {
            query.append(" AND (pm.MaDG LIKE ? OR dg.HoTen LIKE ?)")
            args.add("%$reader%")
            args.add("%$reader%")
        }
        if (librarian.isNotEmpty()) {
            query.append(" AND (pm.MaTT LIKE ? OR tt.HoTen LIKE ?)")
            args.add("%$librarian%")
            args.add("%$librarian%")
        }

        val cursor: Cursor =
            db.rawQuery(query.toString(), args.toArray(arrayOfNulls<String>(args.size)))

        if (cursor.moveToFirst()) {
            do {
                borrowRecords.add(cursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return borrowRecords
    }
}