package com.example.project_android_library_management.dao

import android.database.Cursor
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.model.Account

class AccountDao(private val databaseHelper: DatabaseHelper) {

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
}