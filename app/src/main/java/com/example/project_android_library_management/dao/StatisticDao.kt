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

    fun getBookBorrow(startDate: String?, endDate: String?): Int {
        val db = databaseHelper.openDatabase()
        var bookBorrow = 0
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
            bookBorrow = cursor.getInt(0)
        }
        cursor.close()
        return bookBorrow
    }

    fun getBookReturn(startDate: String?, endDate: String?): Int {
        val db = databaseHelper.openDatabase()
        var bookReturn = 0
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
            bookReturn = cursor.getInt(0)
        }
        cursor.close()
        return bookReturn
    }

    fun getBookNew(startDate: String?, endDate: String?): Int {
        val db = databaseHelper.openDatabase()
        var bookNew = 0

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
            bookNew = cursor.getInt(0)
        }
        cursor.close()
        return bookNew
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
        if (cursor.moveToFirst()) {
            statusCounts["Dã trả"] = cursor.getInt(cursor.getColumnIndexOrThrow("DaTra"))
            statusCounts["Chưa trả"] = cursor.getInt(cursor.getColumnIndexOrThrow("ChuaTra"))
            statusCounts["Quá hạn"] = cursor.getInt(cursor.getColumnIndexOrThrow("QuaHan"))
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
            WITH TheLoaiSach AS (
                SELECT MaTL, COUNT(*) AS SoLuongSach
                FROM Sach
                GROUP BY MaTL
            ),
            Top4TheLoai AS (
                SELECT MaTL
                FROM TheLoaiSach
                ORDER BY SoLuongSach DESC
                LIMIT 4
            )
            SELECT 
                CASE 
                    WHEN s.MaTL IN (SELECT MaTL FROM Top4TheLoai) THEN l.TenLoai
                    ELSE 'Khác'
                END AS TenLoai,
                COUNT(*) AS SoLuongSach
            FROM Sach s
            LEFT JOIN TheLoaiSach tls ON s.MaTL = tls.MaTL
            LEFT JOIN TheLoai l ON s.MaTL = l.MaLoai
            GROUP BY 
                CASE 
                    WHEN s.MaTL IN (SELECT MaTL FROM Top4TheLoai) THEN s.MaTL
                    ELSE 'Khác'
                END
            ORDER BY SoLuongSach DESC
        """.trimIndent()

        val cursor: Cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val category = cursor.getString(cursor.getColumnIndexOrThrow("TenLoai"))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow("SoLuongSach"))
                bookCategoryData[category] = quantity
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return bookCategoryData
    }

    fun getTotalReader(): Int {
        val db = databaseHelper.openDatabase()
        var totalReader = 0
        val cursor: Cursor = db.rawQuery("SELECT COUNT(*) FROM DocGia", null)
        if (cursor.moveToFirst()) {
            totalReader = cursor.getInt(0)
        }
        cursor.close()
        return totalReader
    }

    fun getReaderBorrow(startDate: String?, endDate: String?): Int {
        val db = databaseHelper.openDatabase()
        var readerBorrow = 0
        val query: String
        val args: Array<String>? = if (startDate != null && endDate != null) {
            query = """
                SELECT COUNT(DISTINCT MaDG) AS SoLuongDocGia
                FROM PhieuMuon
                WHERE NgayMuon BETWEEN ? AND ?
            """.trimIndent()
            arrayOf(startDate, endDate)
        } else {
            query = """
                SELECT COUNT(DISTINCT MaDG) AS SoLuongDocGia
                FROM PhieuMuon
            """.trimIndent()
            null
        }
        val cursor: Cursor = db.rawQuery(query, args)
        if (cursor.moveToFirst()) {
            readerBorrow = cursor.getInt(0)
        }
        cursor.close()
        return readerBorrow
    }

    fun getReaderReturn(startDate: String?, endDate: String?): Int {
        val db = databaseHelper.openDatabase()
        var readerReturn = 0
        val query: String
        val args: Array<String>? = if (startDate != null && endDate != null) {
            query = """
                SELECT COUNT(DISTINCT MaDG) AS SoLuongDocGia
                FROM PhieuTra
                WHERE NgayTra BETWEEN ? AND ?
            """.trimIndent()
            arrayOf(startDate, endDate)
        } else {
            query = """
                SELECT COUNT(DISTINCT MaDG) AS SoLuongDocGia
                FROM PhieuTra
            """.trimIndent()
            null
        }
        val cursor: Cursor = db.rawQuery(query, args)
        if (cursor.moveToFirst()) {
            readerReturn = cursor.getInt(0)
        }
        cursor.close()
        return readerReturn
    }

    fun getReaderNew(startDate: String?, endDate: String?): Int {
        val db = databaseHelper.openDatabase()
        var readerNew = 0
        val query: String
        val args: Array<String>? = if (startDate != null && endDate != null) {
            query = """
                SELECT COUNT(DISTINCT MaDG) AS SoLuongDocGiaMoi
                FROM DocGia
                WHERE NgayLamThe BETWEEN ? AND ?
            """.trimIndent()
            arrayOf(startDate, endDate)
        } else {
            query = """
                SELECT COUNT(DISTINCT MaDG) AS SoLuongDocGiaMoi
                FROM DocGia
            """.trimIndent()
            null
        }
        val cursor: Cursor = db.rawQuery(query, args)
        if (cursor.moveToFirst()) {
            readerNew = cursor.getInt(0)
        }
        cursor.close()
        return readerNew
    }

    fun getReaderCountByGeander(): Map<String, Int> {
        val readerCounts = mutableMapOf<String, Int>()
        val db = databaseHelper.openDatabase()

        val query = """
            SELECT GioiTinh, COUNT(*) AS SoLuongDocGia
            FROM DocGia
            GROUP BY GioiTinh
            ORDER BY SoLuongDocGia DESC
        """.trimIndent()

        val cursor: Cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val gender = cursor.getString(cursor.getColumnIndexOrThrow("GioiTinh"))
                val count = cursor.getInt(cursor.getColumnIndexOrThrow("SoLuongDocGia"))
                readerCounts[gender] = count
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return readerCounts
    }

    fun getTop5ReaderMostBorrowed(startDate: String?, endDate: String?): Map<String, Int> {
        val readerMostBorrowed = mutableMapOf<String, Int>()
        val db = databaseHelper.openDatabase()

        val query: String
        val args: Array<String>? = if (startDate != null && endDate != null) {
            query =
                """
                    SELECT dg.HoTen, COUNT(pm.MaPM) AS SoLuongPhieuMuon
                    FROM PhieuMuon pm
                        JOIN DocGia dg ON pm.MaDG = dg.MaDG
                    WHERE pm.NgayMuon BETWEEN ? AND ?
                    GROUP BY dg.MaDG, dg.HoTen
                    ORDER BY SoLuongPhieuMuon DESC
                    LIMIT 5
            """.trimIndent()
            arrayOf(startDate, endDate)
        } else {
            query =
                """
                    SELECT dg.HoTen, COUNT(pm.MaPM) AS SoLuongPhieuMuon
                    FROM PhieuMuon pm
                        JOIN DocGia dg ON pm.MaDG = dg.MaDG
                    GROUP BY dg.MaDG, dg.HoTen
                    ORDER BY SoLuongPhieuMuon DESC
                    LIMIT 5
            """.trimIndent()
            null
        }

        val cursor: Cursor = db.rawQuery(query, args)
        if (cursor.moveToFirst()) {
            do {
                val readerName = cursor.getString(cursor.getColumnIndexOrThrow("HoTen"))
                val count = cursor.getInt(cursor.getColumnIndexOrThrow("SoLuongPhieuMuon"))
                readerMostBorrowed[readerName] = count
            } while (cursor.moveToNext())
        }
        cursor.close()

        return readerMostBorrowed
    }
}