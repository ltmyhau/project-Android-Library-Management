package com.example.project_android_library_management.fragment.librarian

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Patterns
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.dao.LibrarianDao
import com.example.project_android_library_management.model.Librarian
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class LibrarianUpdateActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var librarianDao: LibrarianDao

    private var imagePath: String? = null
    private var maTT: String = ""

    private lateinit var imgAvatar: ImageView
    private lateinit var imgEditIcon: ImageView
    private lateinit var edtLibrarianId: TextInputEditText
    private lateinit var edtLibrarianName: TextInputEditText
    private lateinit var edtDateOfBirth: TextInputEditText
    private lateinit var rgGender: RadioGroup
    private lateinit var edtPhoneNumber: TextInputEditText
    private lateinit var edtEmail: TextInputEditText
    private lateinit var edtAddress: TextInputEditText

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_librarian_edit)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        maTT = intent.getStringExtra("LIBRARIAN_ID") ?: ""

        databaseHelper = DatabaseHelper(this)
        librarianDao = LibrarianDao(databaseHelper)

        imgAvatar = findViewById(R.id.imgAvatar)
        imgEditIcon = findViewById(R.id.imgEditIcon)
        edtLibrarianId = findViewById(R.id.edtLibrarianId)
        edtLibrarianName = findViewById(R.id.edtLibrarianName)
        edtDateOfBirth = findViewById(R.id.edtDateOfBirth)
        rgGender = findViewById(R.id.rgGender)
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber)
        edtEmail = findViewById(R.id.edtEmail)
        edtAddress = findViewById(R.id.edtAddress)

        loadLibrarianDetails(maTT)

        val btnEdit = findViewById<AppCompatButton>(R.id.btnEdit)
        btnEdit.text = "Lưu thông tin"
        btnEdit.setOnClickListener {
            saveLibrarianDetails()
        }

        imgEditIcon.setOnClickListener {
            openImagePicker()
        }
        imgAvatar.setOnClickListener {
            openImagePicker()
        }

        val dateOfBirthLayout = findViewById<TextInputLayout>(R.id.dateOfBirthLayout)
        dateOfBirthLayout.setStartIconOnClickListener {
            showDatePickerDialog(edtDateOfBirth)
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

    private fun loadLibrarianDetails(maTT: String) {
        val librarian = librarianDao.getLibrarianById(maTT)

        if (librarian != null) {
            edtLibrarianId.setText(librarian.MaTT)
            edtLibrarianName.setText(librarian.HoTen)
            edtDateOfBirth.setText(librarian.NgaySinh)
            edtPhoneNumber.setText(librarian.DienThoai)
            edtEmail.setText(librarian.Email)
            edtAddress.setText(librarian.DiaChi)

            when (librarian.GioiTinh.toLowerCase()) {
                "nam" -> rgGender.check(R.id.rdoMale)
                "nữ", "nu" -> rgGender.check(R.id.rdoFemale)
                "khác", "khac" -> rgGender.check(R.id.rdoOther)
            }

            if (librarian.HinhAnh != null) {
                val bitmap = BitmapFactory.decodeByteArray(librarian.HinhAnh, 0, librarian.HinhAnh.size)
                imgAvatar.setImageBitmap(bitmap)
            } else {
                imgAvatar.setImageResource(R.drawable.avatar)
            }
        } else {
            Toast.makeText(this, "Không tìm thấy thủ thư", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePickerDialog(textView: TextView) {
        var calendar: Calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        try {
            dateFormat.parse(textView.text.toString())
            calendar.time = dateFormat.parse(textView.text.toString())!!
        } catch (e: ParseException) {
            calendar.time = Calendar.getInstance().time
        }

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val formattedMonth = String.format("%02d", month + 1)
                val formattedDay = String.format("%02d", dayOfMonth)
                val selectedDate = "${year}-${formattedMonth}-${formattedDay}"
                textView.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
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

    private fun convertImageToByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.toByteArray()
    }

    private fun saveLibrarianDetails() {
        val librarianName = edtLibrarianName.text.toString()
        val dateOfBirth = edtDateOfBirth.text.toString()
        val gender = when (rgGender.checkedRadioButtonId) {
            R.id.rdoMale -> "Nam"
            R.id.rdoFemale -> "Nữ"
            R.id.rdoOther -> "Khác"
            else -> ""
        }
        val phoneNumber = edtPhoneNumber.text.toString()
        val email = edtEmail.text.toString()
        val address = edtAddress.text.toString()

        val imageByteArray = imagePath?.let {
            val bitmap = BitmapFactory.decodeFile(it)
            bitmap?.let { convertImageToByteArray(it) }
        }

        if (validateFields()) {
            val librarian = Librarian(maTT, librarianName, dateOfBirth, gender, phoneNumber, email, address, imageByteArray)
            val rowsAffected = librarianDao.update(librarian)
            if (rowsAffected > 0) {
                Toast.makeText(this, "Cập nhật thông tin thủ thư thành công", Toast.LENGTH_SHORT).show()
                val resultIntent = Intent()
                resultIntent.putExtra("LIBRARIAN_ID", maTT)
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Cập nhật thông tin thủ thư thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateFields(): Boolean {
        if (edtLibrarianName.text.isNullOrEmpty()) {
            edtLibrarianName.error = "Tên thủ thư không được để trống"
            edtLibrarianName.requestFocus()
            return false
        }
        if (edtDateOfBirth.text.isNullOrEmpty()) {
            edtDateOfBirth.error = "Ngày sinh (yyyy-MM-dd) không được để trống"
            edtDateOfBirth.requestFocus()
            return false
        } else if (!validateDate(edtDateOfBirth.text.toString())) {
            edtDateOfBirth.error = "Ngày sinh (yyyy-MM-dd) không hợp lệ"
            edtDateOfBirth.requestFocus()
            return false
        }
        if (edtPhoneNumber.text.isNullOrEmpty()) {
            edtPhoneNumber.error = "Số điện thoại không được để trống"
            edtPhoneNumber.requestFocus()
            return false
        } else if (!isValidPhoneNumber(edtPhoneNumber.text.toString())) {
            edtPhoneNumber.error = "Số điện thoại không hợp lệ"
            edtPhoneNumber.requestFocus()
            return false
        }
        if (edtEmail.text.isNullOrEmpty()) {
            edtEmail.error = "Email không được để trống"
            edtEmail.requestFocus()
            return false
        } else if (!isValidEmail(edtEmail.text.toString())) {
            edtEmail.error = "Email không hợp lệ"
            edtEmail.requestFocus()
            return false
        }
        if (edtAddress.text.isNullOrEmpty()) {
            edtAddress.error = "Địa chỉ không được để trống"
            edtAddress.requestFocus()
            return false
        }
        return true
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        isLenient = false
    }

    private fun validateDate(dateString: String): Boolean {
        return try {
            dateFormat.parse(dateString)
            true
        } catch (e: ParseException) {
            false
        }
    }

    fun isValidEmail(email: String?): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPhoneNumber(phoneNumber: String?): Boolean {
        val phonePattern = "^0[1-9][0-9]{8}$"
        return !TextUtils.isEmpty(phoneNumber) && phoneNumber?.matches(phonePattern.toRegex()) == true
    }
}