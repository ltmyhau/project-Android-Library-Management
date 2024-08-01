package com.example.project_android_library_management

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project_android_library_management.dao.AccountDao
import com.example.project_android_library_management.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var accountDao: AccountDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)
        accountDao = AccountDao(databaseHelper)


        binding.btnLogin.setOnClickListener {
            val username = binding.edtUsename.text.toString()
            val password = binding.edtPassword.text.toString()
            val account = accountDao.getAccount(username, password)

            if (account != null) {
                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("USER_ROLE", account.PhanQuyen)
                editor.putString("ACCOUNT_ID", account.MaTK)
                editor.apply()

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER_ROLE", account.PhanQuyen)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
            }
        }
    }
}