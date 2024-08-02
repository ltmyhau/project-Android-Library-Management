package com.example.project_android_library_management.dao

import android.content.ContentValues
import android.database.Cursor
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.Account
import com.example.project_android_library_management.model.Book

class AccountDao(private val databaseHelper: DatabaseHelper) {

    fun generateNewId(): String {
        var newId = "TK00001"
        val db = databaseHelper.readableDatabase
        db.rawQuery("SELECT MAX(MaTK) FROM TaiKhoan", null).use { cursor ->
            if (cursor.moveToFirst()) {
                val maxCode = cursor.getString(0)
                if (!maxCode.isNullOrEmpty()) {
                    val currentNumber = maxCode.substring(1).toInt()
                    val newNumber = currentNumber + 1
                    newId = "TK" + String.format("%05d", newNumber)
                }
            }
        }
        db.close()
        return newId
    }

    fun insert(account: Account): Int {
        val db = databaseHelper.openDatabase()

        val contentValues = ContentValues().apply {
            put("MaTK", generateNewId())
            put("Username", account.Username)
            put("Password", account.Password)
            put("PhanQuyen", account.PhanQuyen)
            put("MaTT", account.MaTT)
        }
        val result = db.insert("TaiKhoan", null, contentValues)
        db.close()
        return if (result == -1L) 0 else 1
    }

    fun update(account: Account): Int {
        val db = databaseHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put("Username", account.Username)
            put("Password", account.Password)
            put("PhanQuyen", account.PhanQuyen)
            put("MaTT", account.MaTT)
        }

        val rowsAffected = db.update("TaiKhoan", contentValues, "MaTK = ?", arrayOf(account.MaTK))
        db.close()
        return rowsAffected
    }

    fun delete(accountId: String): Int {
        val db = databaseHelper.writableDatabase
        db.execSQL("PRAGMA foreign_keys = ON;")
        val rowsAffected = db.delete("TaiKhoan", "MaTK = ?", arrayOf(accountId))
        db.close()
        return rowsAffected
    }

    private fun cursor(cursor: Cursor): Account {
        val accountId = cursor.getString(cursor.getColumnIndexOrThrow("MaTK"))
        val username = cursor.getString(cursor.getColumnIndexOrThrow("Username"))
        val password = cursor.getString(cursor.getColumnIndexOrThrow("Password"))
        val permission = cursor.getString(cursor.getColumnIndexOrThrow("PhanQuyen"))
        val librarianId = cursor.getString(cursor.getColumnIndexOrThrow("MaTT"))

        return Account(accountId, username, password, permission, librarianId)
    }

    fun getAccount(username: String, password: String): Account? {
        val db = databaseHelper.openDatabase()
        val cursor = db.rawQuery(
            "SELECT * FROM TaiKhoan WHERE Username = ? AND Password = ?",
            arrayOf(username, password)
        )
        var account: Account? = null
        if (cursor.moveToFirst()) {
            account = cursor(cursor)
        }
        cursor.close()
        db.close()
        return account
    }

    fun getAccountById(accountId: String): Account? {
        val db = databaseHelper.openDatabase()
        val cursor = db.rawQuery(
            "SELECT * FROM TaiKhoan WHERE MaTK = ?",
            arrayOf(accountId)
        )
        var account: Account? = null
        if (cursor.moveToFirst()) {
            account = cursor(cursor)
        }
        cursor.close()
        db.close()
        return account
    }
}