package com.example.project_android_library_management.fragment.librarian

import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.dao.LibrarianDao
import java.io.File

class LibrarianDetailActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var librarianDao: LibrarianDao
    private var maTT: String = ""

    private lateinit var imgAvatar: ImageView
    private lateinit var tvLibrarianName: TextView
    private lateinit var tvPhoneNumber: TextView
    private lateinit var tvDateOfBirth: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvAddress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_librarian_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        maTT = intent.getStringExtra("LIBRARIAN_ID") ?: ""

        databaseHelper = DatabaseHelper(this)
        librarianDao = LibrarianDao(databaseHelper)

        imgAvatar = findViewById(R.id.imgAvatar)
        tvLibrarianName = findViewById(R.id.tvLibrarianName)
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber)
        tvDateOfBirth = findViewById(R.id.tvDateOfBirth)
        tvGender = findViewById(R.id.tvGender)
        tvEmail = findViewById(R.id.tvEmail)
        tvAddress = findViewById(R.id.tvAddress)

        loadLibrarianDetails(maTT)
    }

    private fun loadLibrarianDetails(maTT: String) {
        val librarian = librarianDao.getLibrarianById(maTT)

        if (librarian != null) {
            tvLibrarianName.text = librarian.HoTen
            tvPhoneNumber.text = librarian.DienThoai
            tvDateOfBirth.text = librarian.NgaySinh
            tvGender.text = librarian.GioiTinh
            tvEmail.text = librarian.Email
            tvAddress.text = librarian.DiaChi

            if (librarian.HinhAnh != null) {
                val bitmap = BitmapFactory.decodeByteArray(librarian.HinhAnh, 0, librarian.HinhAnh.size)
                imgAvatar.setImageBitmap(bitmap)
            } else {
                imgAvatar.setImageResource(R.drawable.avatar)
            }
        } else {
            Toast.makeText(this, "Không tìm thấy thủ thư", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_delete_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_edit -> {
                editReader(maTT)
                true
            }

            R.id.menu_delete -> {
                deleteReader()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val REQUEST_CODE_UPDATE_LIBRARIAN = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UPDATE_LIBRARIAN && resultCode == RESULT_OK) {
            loadLibrarianDetails(maTT)
        }
    }

    private fun editReader(maTT: String) {
        val intent = Intent(this, LibrarianUpdateActivity::class.java)
        intent.putExtra("LIBRARIAN_ID", maTT)
        startActivityForResult(intent, REQUEST_CODE_UPDATE_LIBRARIAN)
    }

    private fun deleteReader() {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận")
            .setMessage("Bạn có chắc chắn muốn xóa thủ thư này không?")
            .setPositiveButton("Có") { _, _ ->
                try {
                    val rowsAffected = librarianDao.delete(maTT)
                    if (rowsAffected > 0) {
                        Toast.makeText(this, "Đã xóa thủ thư thành công", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Không tìm thấy thủ thư để xóa", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: SQLiteConstraintException) {
                    Toast.makeText(this, "Không thể xóa vì có ràng buộc khóa ngoại!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Lỗi xảy ra: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    finish()
                }
            }
            .setNegativeButton("Không") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}