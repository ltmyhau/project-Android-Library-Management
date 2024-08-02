package com.example.project_android_library_management.fragment.account

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.dao.AccountDao
import com.google.android.material.textfield.TextInputEditText

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var accountDao: AccountDao

    private lateinit var edtOldPassword: TextInputEditText
    private lateinit var edtNewPassword: TextInputEditText
    private lateinit var edtConfirmPassword: TextInputEditText
    private lateinit var btnChangePassword: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        edtOldPassword = findViewById(R.id.edtOldPassword)
        edtNewPassword = findViewById(R.id.edtNewPassword)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)
        btnChangePassword = findViewById(R.id.btnChangePassword)

        databaseHelper = DatabaseHelper(this)
        accountDao = AccountDao(databaseHelper)

        btnChangePassword.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {
        val oldPassword = edtOldPassword.text.toString()
        val newPassword = edtNewPassword.text.toString()
        val confirmPassword = edtConfirmPassword.text.toString()

        if (validateFields()) {
            val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val accountId = sharedPreferences.getString("ACCOUNT_ID", null)

            if (accountId != null) {
                val account = accountDao.getAccountById(accountId)
                if (account != null && account.Password == oldPassword) {
                    account.Password = newPassword
                    val rowsAffected = accountDao.update(account)
                    if (rowsAffected > 0) {
                        Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    edtOldPassword.error = "Mật khẩu cũ không đúng"
                    edtOldPassword.requestFocus()
                }
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin tài khoản", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateFields(): Boolean {
        if (edtOldPassword.text.isNullOrEmpty()) {
            edtOldPassword.error = "Vui lòng nhập mật khẩu hiện tại"
            edtOldPassword.requestFocus()
            return false
        }
        if (edtNewPassword.text.isNullOrEmpty()) {
            edtNewPassword.error = "Vui lòng nhập mật khẩu mới"
            edtNewPassword.requestFocus()
            return false
        }
        if (edtConfirmPassword.text.isNullOrEmpty()) {
            edtConfirmPassword.error = "Vui lòng xác nhận mật khẩu mới"
            edtConfirmPassword.requestFocus()
            return false
        }
        if (edtNewPassword.text.toString() != edtConfirmPassword.text.toString()) {
            edtConfirmPassword.error = "Mật khẩu mới và xác nhận mật khẩu không khớp"
            edtConfirmPassword.requestFocus()
            return false
        }
        return true
    }
}