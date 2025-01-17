package com.example.project_android_library_management.fragment.book

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.fragment.search.SearchPublisherActivity
import com.example.project_android_library_management.dao.BookCategoryDao
import com.example.project_android_library_management.dao.BookDao
import com.example.project_android_library_management.dao.PublisherDao
import com.example.project_android_library_management.model.Book
import com.google.android.material.textfield.TextInputEditText
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class BookUpdateActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var spnCategory: AutoCompleteTextView
    private lateinit var bookDao: BookDao
    private lateinit var bookCategoryDao: BookCategoryDao
    private lateinit var publisherDao: PublisherDao

    private var bookCategoryId: String = ""
    private var imagePath: String? = null
    private var publisherId: String = ""
    private var bookCover: ByteArray? = null

    private lateinit var imgBookCover: ImageView
    private lateinit var imgEditIcon: ImageView
    private lateinit var edtBookId: TextInputEditText
    private lateinit var edtISBN: TextInputEditText
    private lateinit var edtTitle: TextInputEditText
    private lateinit var edtAuthor: TextInputEditText
    private lateinit var edtPublisher: TextInputEditText
    private lateinit var edtYear: TextInputEditText
    private lateinit var edtPages: TextInputEditText
    private lateinit var edtStock: TextInputEditText
    private lateinit var edtPrice: TextInputEditText
    private lateinit var edtDescription: TextInputEditText

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1
        private const val REQUEST_CODE_PUBLISHER_ID = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_edit)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val maSach = intent.getStringExtra("BOOK_ID") ?: ""

        databaseHelper = DatabaseHelper(this)
        bookDao = BookDao(databaseHelper)
        bookCategoryDao = BookCategoryDao(databaseHelper)
        publisherDao = PublisherDao(databaseHelper)

        imgBookCover = findViewById(R.id.imgBookCover)
        imgEditIcon = findViewById(R.id.imgEditIcon)
        edtBookId = findViewById(R.id.edtBookId)
        edtISBN = findViewById(R.id.edtISBN)
        edtTitle = findViewById(R.id.edtTitle)
        edtAuthor = findViewById(R.id.edtAuthor)
        spnCategory = findViewById(R.id.spnCategory)
        edtPublisher = findViewById(R.id.edtPublisher)
        edtYear = findViewById(R.id.edtYear)
        edtPages = findViewById(R.id.edtPages)
        edtStock = findViewById(R.id.edtStock)
        edtPrice = findViewById(R.id.edtPrice)
        edtDescription = findViewById(R.id.edtDescription)

        loadCategorySpinner()
        loadBookDetails(maSach)

        val btnEdit = findViewById<AppCompatButton>(R.id.btnEdit)
        btnEdit.text = "Lưu thông tin"
        btnEdit.setOnClickListener {
            saveBookDetails()
        }

        imgEditIcon.setOnClickListener {
            openImagePicker()
        }
        imgBookCover.setOnClickListener {
            openImagePicker()
        }

        edtPublisher.setOnClickListener {
            val intent = Intent(this, SearchPublisherActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_PUBLISHER_ID)
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

    private fun loadCategorySpinner() {
        val categories = bookCategoryDao.getAllBookCategories()
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, categories.map { it.TenLoai })
        adapter.setDropDownViewResource(R.layout.dropdown_item)
        spnCategory.setAdapter(adapter)

        spnCategory.setOnItemClickListener { parent, _, position, _ ->
            val selectedCategoryName = parent.getItemAtPosition(position) as String
            val selectedCategory = categories.find { it.TenLoai == selectedCategoryName }
            selectedCategory?.let {
                bookCategoryId = it.MaLoai
            }
        }
    }

    private fun loadBookDetails(bookId: String) {
        val book = bookDao.getBookById(bookId)

        if (book != null) {
            edtBookId.setText(book.MaSach)
            edtISBN.setText(book.ISBN)
            edtTitle.setText(book.TenSach)
            edtAuthor.setText(book.TacGia)
            edtYear.setText(book.NamXB.toString())
            edtPages.setText(book.SoTrang.toString())
            edtStock.setText(book.SoLuongTon.toString())
            edtPrice.setText(String.format("%.0f", book.GiaBan))
            edtDescription.setText(book.MoTa)

            publisherId = book.MaNXB
            val publisherDao = PublisherDao(databaseHelper)
            val publisher = publisherDao.getPublisherById(publisherId)

            publisher?.let { edtPublisher.setText(publisher.TenNXB) }

            bookCategoryId = book.MaTL

            val bookCategoryName = bookCategoryDao.getBookCategoryNameById(book.MaTL)
            if (bookCategoryName != null) {
                spnCategory.setText(bookCategoryName, false)
            }

            if (book.HinhAnh != null) {
                bookCover = book.HinhAnh
                val bitmap = BitmapFactory.decodeByteArray(book.HinhAnh, 0, book.HinhAnh.size)
                imgBookCover.setImageBitmap(bitmap)
            } else {
                imgBookCover.setImageResource(R.drawable.book_cover)
            }
        } else {
            Toast.makeText(this, "Không tìm thấy sách", Toast.LENGTH_SHORT).show()
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
                    imgBookCover.setImageBitmap(bitmap)
                }
            }
        } else if (requestCode == REQUEST_CODE_PUBLISHER_ID && resultCode == RESULT_OK) {
            publisherId = data?.getStringExtra("PUBLISHER_ID") ?: ""
            if (publisherId != null && publisherId.isNotEmpty()) {
                val publisher = publisherDao.getPublisherById(publisherId)
                edtPublisher.setText(publisher?.TenNXB ?: "")
                edtPublisher.error = null
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

    private fun saveBookDetails() {
        val bookId = edtBookId.text.toString()
        val isbn = edtISBN.text.toString()
        val title = edtTitle.text.toString()
        val author = edtAuthor.text.toString()
        val year = edtYear.text.toString().toIntOrNull() ?: 0
        val pages = edtPages.text.toString().toIntOrNull() ?: 0
        val stock = edtStock.text.toString().toIntOrNull() ?: 0
        val price = edtPrice.text.toString().toDoubleOrNull() ?: 0.0
        val description = edtDescription.text.toString()

        bookCover = imagePath?.let {
            val bitmap = BitmapFactory.decodeFile(it)
            bitmap?.let { convertImageToByteArray(it) }
        } ?: bookCover

        if (validateFields()) {
            val book = Book(bookId, isbn, title, author, publisherId, year, pages, stock, price, description, bookCover, bookCategoryId)
            val rowsAffected = bookDao.update(book)
            if (rowsAffected > 0) {
                Toast.makeText(this, "Cập nhật thông tin sách thành công", Toast.LENGTH_SHORT).show()
                val resultIntent = Intent()
                resultIntent.putExtra("BOOK_ID", book.MaSach)
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Cập nhật thông tin sách thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateFields(): Boolean {
        if (edtISBN.text.isNullOrEmpty()) {
            edtISBN.error = "ISBN không được để trống"
            edtISBN.requestFocus()
            return false
        }
        if (edtTitle.text.isNullOrEmpty()) {
            edtTitle.error = "Tên sách không được để trống"
            edtTitle.requestFocus()
            return false
        }
        if (edtAuthor.text.isNullOrEmpty()) {
            edtAuthor.error = "Tác giả không được để trống"
            edtAuthor.requestFocus()
            return false
        }
        if (edtPublisher.text.isNullOrEmpty() || edtPublisher.text.toString() == "Nhà xuất bản") {
            edtPublisher.error = ""
            Toast.makeText(this, "Vui lòng chọn nhà xuất bản", Toast.LENGTH_SHORT).show()
            return false
        }
        if (edtYear.text.isNullOrEmpty()) {
            edtYear.error = "Năm xuất bản không được để trống"
            edtYear.requestFocus()
            return false
        } else {
            val year = edtYear.text.toString().toIntOrNull()
            if (year == null || year <= 0) {
                edtYear.error = "Năm xuất bản không hợp lệ"
                edtYear.requestFocus()
                return false
            }
        }
        if (edtPages.text.isNullOrEmpty()) {
            edtPages.error = "Số trang không được để trống"
            edtPages.requestFocus()
            return false
        } else {
            val pages = edtPages.text.toString().toIntOrNull()
            if (pages == null || pages <= 0) {
                edtPages.error = "Số trang phải là số dương"
                edtPages.requestFocus()
                return false
            }
        }
        if (edtStock.text.isNullOrEmpty()) {
            edtStock.error = "Số lượng tồn không được để trống"
            edtStock.requestFocus()
            return false
        } else {
            val stock = edtStock.text.toString().toIntOrNull()
            if (stock == null || stock < 0) {
                edtStock.error = "Số lượng tồn phải là số nguyên không âm"
                edtStock.requestFocus()
                return false
            }
        }
        if (edtPrice.text.isNullOrEmpty()) {
            edtPrice.error = "Giá bán không được để trống"
            edtPrice.requestFocus()
            return false
        } else {
            val price = edtPrice.text.toString().toDoubleOrNull()
            if (price == null || price < 0) {
                edtPrice.error = "Giá bán phải là số dương"
                edtPrice.requestFocus()
                return false
            }
        }
        if (spnCategory.text.isNullOrEmpty() || spnCategory.text.toString() == "Thể loại") {
            Toast.makeText(this, "Vui lòng chọn thể loại", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}