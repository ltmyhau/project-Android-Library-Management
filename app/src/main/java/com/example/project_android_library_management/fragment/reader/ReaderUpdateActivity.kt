package com.example.project_android_library_management.fragment.reader

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.dao.ReaderDao
import com.example.project_android_library_management.fragment.book.BookUpdateActivity
import com.example.project_android_library_management.model.Book
import com.example.project_android_library_management.model.Reader
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ReaderUpdateActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var readerDao: ReaderDao

    private var imagePath: String? = null
    private var maDG: Int = -1

    private lateinit var imgAvatar: ImageView
    private lateinit var edtReaderName: TextInputEditText
    private lateinit var edtDateOfBirth: TextInputEditText
    private lateinit var rgGender: RadioGroup
    private lateinit var edtPhoneNumber: TextInputEditText
    private lateinit var edtEmail: TextInputEditText
    private lateinit var edtJoinDate: TextInputEditText
    private lateinit var edtDuration: TextInputEditText
    private lateinit var edtAddress: TextInputEditText

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader_update)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        maDG = intent.getIntExtra("MaDG", 0)

        databaseHelper = DatabaseHelper(this)
        readerDao = ReaderDao(databaseHelper)

        imgAvatar = findViewById(R.id.imgAvatar)
        edtReaderName = findViewById(R.id.edtReaderName)
        edtDateOfBirth = findViewById(R.id.edtDateOfBirth)
        rgGender = findViewById(R.id.rgGender)
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber)
        edtEmail = findViewById(R.id.edtEmail)
        edtJoinDate = findViewById(R.id.edtJoinDate)
        edtDuration = findViewById(R.id.edtDuration)
        edtAddress = findViewById(R.id.edtAddress)

        loadReaderDetails(maDG)

        val btnSave = findViewById<AppCompatButton>(R.id.btnSave)
        btnSave.setOnClickListener {
            saveReaderDetails()
        }

        imgAvatar.setOnClickListener {
            openImagePicker()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận")
            .setMessage("Bạn có chắc chắn muốn quay lại không? Những thay đổi của bạn sẽ không được lưu.")
            .setPositiveButton("Có") { _, _ ->
                super.onBackPressed()
            }
            .setNegativeButton("Không") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun loadReaderDetails(maDG: Int) {
        val reader = readerDao.getReaderById(maDG)

        if (reader != null) {
            edtReaderName.setText(reader.HoTen)
            edtDateOfBirth.setText(reader.NgaySinh)
            edtPhoneNumber.setText(reader.DienThoai)
            edtEmail.setText(reader.Email)
            edtJoinDate.setText(reader.NgayLamThe)
            edtDuration.setText(reader.HanThe)
            edtAddress.setText(reader.DiaChi)

            when (reader.GioiTinh.toLowerCase()) {
                "nam" -> rgGender.check(R.id.rdoMale)
                "nữ", "nu" -> rgGender.check(R.id.rdoFemale)
                "khác", "khac" -> rgGender.check(R.id.rdoOther)
            }

            imagePath = reader.HinhAnh
            imagePath?.let {
                val imgFile = File(it)
                if (imgFile.exists()) {
                    val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                    imgAvatar.setImageBitmap(bitmap)
                }
            }
        } else {
            Toast.makeText(this, "Không tìm thấy độc giả", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            imageUri?.let {
                val imagePath = saveImageToInternalStorage(it)
                imagePath?.let { path ->
                    this.imagePath = path
                    val bitmap = BitmapFactory.decodeFile(path)
                    imgAvatar.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        val bitmap: Bitmap
        try {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        val file = File(filesDir, "${System.currentTimeMillis()}.jpg")
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        return file.absolutePath
    }

    private fun saveReaderDetails() {
        val readerName = edtReaderName.text.toString()
        val dateOfBirth = edtDateOfBirth.text.toString()
        val gender = when (rgGender.checkedRadioButtonId) {
            R.id.rdoMale -> "Nam"
            R.id.rdoFemale -> "Nữ"
            R.id.rdoOther -> "Khác"
            else -> ""
        }
        val phoneNumber = edtPhoneNumber.text.toString()
        val email = edtEmail.text.toString()
        val joinDate = edtJoinDate.text.toString()
        val duration = edtDuration.text.toString()
        val address = edtAddress.text.toString()

        if (validateFields()) {
            val reader = Reader(maDG, readerName, dateOfBirth, gender, phoneNumber, email, address, imagePath, joinDate, duration)
            val rowsAffected = readerDao.update(reader)
            if (rowsAffected > 0) {
                Toast.makeText(this, "Cập nhật thông tin độc giả thành công", Toast.LENGTH_SHORT).show()
                val resultIntent = Intent()
                resultIntent.putExtra("MaDG", maDG)
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Cập nhật thông tin độc giả thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateFields(): Boolean {
        if (edtReaderName.text.isNullOrEmpty()) {
            edtReaderName.error = "Tên độc giả không được để trống"
            edtReaderName.requestFocus()
            return false
        }
        if (edtDateOfBirth.text.isNullOrEmpty()) {
            edtDateOfBirth.error = "Ngày sinh không được để trống"
            edtDateOfBirth.requestFocus()
            return false
        }
        if (edtPhoneNumber.text.isNullOrEmpty()) {
            edtPhoneNumber.error = "Số điện thoại không được để trống"
            edtPhoneNumber.requestFocus()
            return false
        }
        if (edtEmail.text.isNullOrEmpty()) {
            edtEmail.error = "Email không được để trống"
            edtEmail.requestFocus()
            return false
        }
        if (edtJoinDate.text.isNullOrEmpty()) {
            edtJoinDate.error = "Ngày làm thẻ không được để trống"
            edtJoinDate.requestFocus()
            return false
        }
        if (edtDuration.text.isNullOrEmpty()) {
            edtDuration.error = "Hạn thẻ không được để trống"
            edtDuration.requestFocus()
            return false
        }
        if (edtAddress.text.isNullOrEmpty()) {
            edtAddress.error = "Địa chỉ không được để trống"
            edtAddress.requestFocus()
            return false
        }
        return true
    }
}