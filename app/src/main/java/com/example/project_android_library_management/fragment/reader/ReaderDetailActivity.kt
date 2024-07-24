package com.example.project_android_library_management.fragment.reader

import android.content.Intent
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
import com.example.project_android_library_management.dao.ReaderDao
import com.example.project_android_library_management.fragment.book.BookUpdateActivity
import java.io.File

class ReaderDetailActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var readerDao: ReaderDao
    private var maDG: Int = 0

    private lateinit var imgAvatar: ImageView
    private lateinit var tvReaderName: TextView
    private lateinit var tvPhoneNumber: TextView
    private lateinit var tvJoinDate: TextView
    private lateinit var tvDateOfBirth: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvAddress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        maDG = intent.getIntExtra("MaDG", 0)

        databaseHelper = DatabaseHelper(this)
        readerDao = ReaderDao(databaseHelper)

        imgAvatar = findViewById(R.id.imgAvatar)
        tvReaderName = findViewById(R.id.tvReaderName)
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber)
        tvJoinDate = findViewById(R.id.tvJoinDate)
        tvDateOfBirth = findViewById(R.id.tvDateOfBirth)
        tvGender = findViewById(R.id.tvGender)
        tvEmail = findViewById(R.id.tvEmail)
        tvAddress = findViewById(R.id.tvAddress)

        loadReaderDetails(maDG)
    }

    private fun loadReaderDetails(maDG: Int) {
        val reader = readerDao.getReaderById(maDG)

        if (reader != null) {
            tvReaderName.text = reader.HoTen
            tvPhoneNumber.text = reader.DienThoai
            tvJoinDate.text = reader.NgayLamThe
            tvDateOfBirth.text = reader.NgaySinh
            tvGender.text = reader.GioiTinh
            tvEmail.text = reader.Email
            tvAddress.text = reader.DiaChi

            val imagePath = reader.HinhAnh
            if (imagePath != null) {
                val imgFile = File(imagePath)
                if (imgFile.exists()) {
                    val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                    imgAvatar.setImageBitmap(bitmap)
                }
            }
        } else {
            Toast.makeText(this, "Không tìm thấy độc giả", Toast.LENGTH_SHORT).show()
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
                editReader(maDG)
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
        private const val REQUEST_CODE_UPDATE_READER = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UPDATE_READER && resultCode == RESULT_OK) {
            loadReaderDetails(maDG)
        }
    }

    private fun editReader(maDG: Int) {
        val intent = Intent(this, ReaderUpdateActivity::class.java)
        intent.putExtra("MaDG", maDG)
        startActivityForResult(intent, REQUEST_CODE_UPDATE_READER)
    }

    private fun deleteReader() {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận")
            .setMessage("Bạn có chắc chắn muốn xóa độc giả này không?")
            .setPositiveButton("Có") { _, _ ->
                val rowsAffected = readerDao.delete(maDG)
                if (rowsAffected > 0) {
                    Toast.makeText(this, "Đã xóa độc giả thành công", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Xóa độc giả thất bại", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
            .setNegativeButton("Không") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}