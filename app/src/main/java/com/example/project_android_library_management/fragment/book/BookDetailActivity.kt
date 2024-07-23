package com.example.project_android_library_management.fragment.book

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
import androidx.appcompat.widget.AppCompatButton
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.dao.BookCategoryDao
import com.example.project_android_library_management.dao.BookDao
import java.io.File

class BookDetailActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var bookDao: BookDao
    private lateinit var bookCategoryDao: BookCategoryDao
    private lateinit var isbn: String

    private lateinit var imgBookCover: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvAuthor: TextView
    private lateinit var tvPublisher: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvIBPN: TextView
    private lateinit var tvYear: TextView
    private lateinit var tvPages: TextView
    private lateinit var tvStock: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvDescription: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isbn = intent.getStringExtra("ISBN") ?: ""

        databaseHelper = DatabaseHelper(this)
        bookDao = BookDao(databaseHelper)
        bookCategoryDao = BookCategoryDao(databaseHelper)

        imgBookCover = findViewById(R.id.imgBookCover)
        tvTitle = findViewById(R.id.tvTitle)
        tvAuthor = findViewById(R.id.tvAuthor)
        tvPublisher = findViewById(R.id.tvPublisher)
        tvCategory = findViewById(R.id.tvCategory)
        tvIBPN = findViewById(R.id.tvIBPN)
        tvYear = findViewById(R.id.tvYear)
        tvPages = findViewById(R.id.tvPages)
        tvStock = findViewById(R.id.tvStock)
        tvPrice = findViewById(R.id.tvPrice)
        tvDescription = findViewById(R.id.tvDescription)

        loadBookDetails(isbn)
    }

    private fun loadBookDetails(isbn: String) {
        val book = bookDao.getBookByIsbn(isbn)

        if (book != null) {
            tvTitle.text = book.TenSach
            tvAuthor.text = book.TacGia
            tvPublisher.text = book.NXB
            tvCategory.text = bookCategoryDao.getBookCategoryNameById(book.MaTL)
            tvIBPN.text = book.ISBN
            tvYear.text = book.NamXB.toString()
            tvPages.text = book.SoTrang.toString()
            tvStock.text = book.SoLuongTon.toString()
            tvPrice.text = book.GiaBan.toString()
            tvDescription.text = book.MoTa
//            imgBookCover.setImageResource(R.drawable.book_cover)
            val imagePath = book.HinhAnh
            if (imagePath != null) {
                val imgFile = File(imagePath)
                if (imgFile.exists()) {
                    val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                    imgBookCover.setImageBitmap(bitmap)
                }
            }
        } else {
            Toast.makeText(this, "Không tìm thấy sách", Toast.LENGTH_SHORT).show()
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
                editBook(isbn)
                true
            }

            R.id.menu_delete -> {
                deleteBook()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val REQUEST_CODE_UPDATE_BOOK = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UPDATE_BOOK && resultCode == RESULT_OK) {
//            val isbn = data?.getStringExtra("BOOK_ISBN") ?: return
            loadBookDetails(isbn)
        }
    }

    private fun editBook(isbn: String) {
        val intent = Intent(this, BookUpdateActivity::class.java)
        intent.putExtra("ISBN", isbn)
        startActivityForResult(intent, REQUEST_CODE_UPDATE_BOOK)
    }

    private fun deleteBook() {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận")
            .setMessage("Bạn có chắc chắn muốn xóa sách này không?")
            .setPositiveButton("Có") { _, _ ->
//                databaseHelper.deleteBook(bookId)
                Toast.makeText(this, "Đã xóa sách thành công", Toast.LENGTH_SHORT).show()
//                val intent = Intent()
//                setResult(RESULT_OK, intent)
                finish()
            }
            .setNegativeButton("Không") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}