package com.example.project_android_library_management.dao

import android.database.Cursor
import android.util.Log
import com.example.project_android_library_management.DatabaseHelper

class StatisticDao(private val databaseHelper: DatabaseHelper) {

    fun getTotalBook(): Int {
        val db = databaseHelper.openDatabase()
        var totalBook = 0
        val cursor: Cursor = db.rawQuery("SELECT SUM(SoLuongTon) FROM Sach", null)
        if (cursor.moveToFirst()) {
            totalBook = cursor.getInt(0)
        }
        cursor.close()
        return totalBook
    }

    fun getQuantityBorrow(startDate: String?, endDate: String?): Int {
        val db = databaseHelper.openDatabase()
        var quantityBorrow = 0
        val query: String
        val args: Array<String>? = if (startDate != null && endDate != null) {
            query = """
                SELECT SUM(ctpm.SoLuong) 
                FROM CTPhieuMuon ctpm
                    JOIN PhieuMuon pm ON ctpm.MaPM = pm.MaPM
                WHERE pm.NgayMuon BETWEEN ? AND ?
            """.trimIndent()
            arrayOf(startDate, endDate)
        } else {
            query = """
                SELECT SUM(ctpm.SoLuong) 
                FROM CTPhieuMuon ctpm
                    JOIN PhieuMuon pm ON ctpm.MaPM = pm.MaPM
            """.trimIndent()
            null
        }
        val cursor: Cursor = db.rawQuery(query, args)
        if (cursor.moveToFirst()) {
            quantityBorrow = cursor.getInt(0)
        }
        cursor.close()
        return quantityBorrow
    }

    fun getQuantityReturn(startDate: String?, endDate: String?): Int {
        val db = databaseHelper.openDatabase()
        var quantityReturn = 0
        val query: String
        val args: Array<String>? = if (startDate != null && endDate != null) {
            query = """
                SELECT SUM(ctpt.SoLuong) 
                FROM CTPhieuTra ctpt
                    JOIN PhieuTra pt ON ctpt.MaPT = pt.MaPT
                WHERE pt.NgayTra BETWEEN ? AND ?
            """.trimIndent()
            arrayOf(startDate, endDate)
        } else {
            query = """
                SELECT SUM(ctpt.SoLuong) 
                FROM CTPhieuTra ctpt
                    JOIN PhieuTra pt ON ctpt.MaPT = pt.MaPT
            """.trimIndent()
            null
        }
        val cursor: Cursor = db.rawQuery(query, args)
        if (cursor.moveToFirst()) {
            quantityReturn = cursor.getInt(0)
        }
        cursor.close()
        return quantityReturn
    }

    fun getQuantityNew(startDate: String?, endDate: String?): Int {
        val db = databaseHelper.openDatabase()
        var quantityNew = 0

        val query: String
        val args: Array<String>? = if (startDate != null && endDate != null) {
            query = """
                SELECT SUM(ctpd.SoLuong) 
                FROM CTPhieuDat ctpd
                    JOIN PhieuDat pd ON ctpd.MaPD = pd.MaPD
                WHERE pd.NgayDat BETWEEN ? AND ?
            """.trimIndent()
            arrayOf(startDate, endDate)
        } else {
            query = """
                SELECT SUM(ctpd.SoLuong) 
                FROM CTPhieuDat ctpd
                    JOIN PhieuDat pd ON ctpd.MaPD = pd.MaPD
            """.trimIndent()
            null
        }
        val cursor: Cursor = db.rawQuery(query, args)
        if (cursor.moveToFirst()) {
            quantityNew = cursor.getInt(0)
        }
        cursor.close()
        return quantityNew
    }

    fun getStatusCounts(startDate: String?, endDate: String?):  Map<String, Int> {
        val statusCounts = mutableMapOf<String, Int>()
        val db = databaseHelper.openDatabase()

        val query: String
        val args: Array<String>? = if (startDate != null && endDate != null) {
            query =
                """
                SELECT
                    SUM(CASE
                            WHEN pt.MaPM IS NOT NULL
                            AND DATE(pt.NgayTra) BETWEEN DATE(?) AND DATE(?) THEN 1
                            ELSE 0
                        END) AS DaTra,
                    SUM(CASE
                            WHEN pt.MaPM IS NULL
                                AND DATE(pm.NgayMuon) BETWEEN DATE(?) AND DATE(?)
                                 AND DATE(pm.NgayMuon, '+' || pm.SoNgayMuon || ' days') NOT BETWEEN DATE(?) AND DATE(?) THEN 1
                            ELSE 0
                        END) AS ChuaTra,
                    SUM(CASE
                            WHEN pt.MaPM IS NULL
                                 AND DATE(pm.NgayMuon, '+' || pm.SoNgayMuon || ' days') BETWEEN DATE(?) AND DATE(?) THEN 1
                            ELSE 0
                        END) AS QuaHan
                FROM PhieuMuon pm
                    LEFT JOIN PhieuTra pt ON pm.MaPM = pt.MaPM
            """.trimIndent()
            arrayOf(startDate, endDate, startDate, endDate, startDate, endDate, startDate, endDate)
        } else {
            query =
                """
                SELECT
                    SUM(CASE
                            WHEN pt.MaPM IS NOT NULL THEN 1
                            ELSE 0
                        END) AS DaTra,
                    SUM(CASE
                            WHEN pt.MaPM IS NULL THEN 1
                            ELSE 0
                        END) AS ChuaTra,
                    SUM(CASE
                            WHEN pt.MaPM IS NULL THEN 1
                            ELSE 0
                        END) AS QuaHan
                FROM PhieuMuon pm
                    LEFT JOIN PhieuTra pt ON pm.MaPM = pt.MaPM
            """.trimIndent()
            null
        }

        val cursor: Cursor = db.rawQuery(query, args)

//        if (cursor.moveToFirst()) {
//            do {
//                val daTra = cursor.getInt(cursor.getColumnIndexOrThrow("DaTra"))
//                val chuaTra = cursor.getInt(cursor.getColumnIndexOrThrow("ChuaTra"))
//                val quaHan = cursor.getInt(cursor.getColumnIndexOrThrow("QuaHan"))
//
//                statusCounts.add(StatusCount("DaTra", daTra))
//                statusCounts.add(StatusCount("ChuaTra", chuaTra))
//                statusCounts.add(StatusCount("QuaHan", quaHan))
//            } while (cursor.moveToNext())
//        }

        if (cursor.moveToFirst()) {
            statusCounts["DaTra"] = cursor.getInt(cursor.getColumnIndexOrThrow("DaTra"))
            statusCounts["ChuaTra"] = cursor.getInt(cursor.getColumnIndexOrThrow("ChuaTra"))
            statusCounts["QuaHan"] = cursor.getInt(cursor.getColumnIndexOrThrow("QuaHan"))
        }
        cursor.close()

        return statusCounts
    }

    fun getTop5BookMostBorrowed(startDate: String?, endDate: String?): Map<String, Int> {
        val bookMostBorrowed = mutableMapOf<String, Int>()
        val db = databaseHelper.openDatabase()

        val query: String
        val args: Array<String>? = if (startDate != null && endDate != null) {
            query =
                """
                    SELECT s.TenSach, COUNT(ctpm.MaSach) AS SoLanMuon
                    FROM CTPhieuMuon ctpm
                        JOIN Sach s ON ctpm.MaSach = s.MaSach
                        JOIN PhieuMuon pm ON ctpm.MaPM = pm.MaPM
                    WHERE pm.NgayMuon BETWEEN ? AND ?
                    GROUP BY s.MaSach, s.TenSach
                    ORDER BY SoLanMuon DESC
                    LIMIT 5
            """.trimIndent()
            arrayOf(startDate, endDate)
        } else {
            query =
                """
                    SELECT s.TenSach, COUNT(ctpm.MaSach) AS SoLanMuon
                    FROM CTPhieuMuon ctpm
                        JOIN Sach s ON ctpm.MaSach = s.MaSach
                    GROUP BY s.MaSach, s.TenSach
                    ORDER BY SoLanMuon DESC
                    LIMIT 5
            """.trimIndent()
            null
        }

        val cursor: Cursor = db.rawQuery(query, args)
        if (cursor.moveToFirst()) {
            do {
                val bookName = cursor.getString(cursor.getColumnIndexOrThrow("TenSach"))
                val count = cursor.getInt(cursor.getColumnIndexOrThrow("SoLanMuon"))
                bookMostBorrowed[bookName] = count
            } while (cursor.moveToNext())
        }
        cursor.close()

        return bookMostBorrowed
    }

    fun getBookCategoryData(): Map<String, Int>  {
        val db = databaseHelper.openDatabase()
        val bookCategoryData = mutableMapOf<String, Int>()

        val query = """
            WITH RankedCategories AS (
                SELECT 
                    tl.TenLoai, 
                    SUM(s.SoLuongTon) AS SoLuongTon,
                    ROW_NUMBER() OVER (ORDER BY SUM(s.SoLuongTon) DESC) AS rn
                FROM Sach s
                JOIN TheLoai tl ON s.MaTL = tl.MaLoai
                GROUP BY tl.TenLoai
            ),
            TopCategories AS (
                SELECT 
                    TenLoai,
                    SoLuongTon
                FROM RankedCategories
                WHERE rn <= 9
            ),
            OtherCategories AS (
                SELECT 
                    'Các thể loại khác' AS TenLoai,
                    SUM(SoLuongTon) AS SoLuongTon
                FROM RankedCategories
                WHERE rn > 9
            )
            SELECT * FROM TopCategories
            UNION ALL
            SELECT * FROM OtherCategories
        """.trimIndent()

        val cursor: Cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val category = cursor.getString(cursor.getColumnIndexOrThrow("TenLoai"))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow("SoLuongTon"))
                bookCategoryData[category] = quantity
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return bookCategoryData
    }
}