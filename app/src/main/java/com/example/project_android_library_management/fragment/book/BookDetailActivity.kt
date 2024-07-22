package com.example.project_android_library_management.fragment.book

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
import com.example.project_android_library_management.dao.BookDao
import com.example.project_android_library_management.dao.CategoryDao

class BookDetailActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var bookDao: BookDao
    private lateinit var categoryDao: CategoryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val isbn = intent.getStringExtra("ISBN")

        databaseHelper = DatabaseHelper(this)
        bookDao = BookDao(databaseHelper)
        categoryDao = CategoryDao(databaseHelper)

        val book = bookDao.getBookByIsbn(isbn)

        val imgBookCover = findViewById<ImageView>(R.id.imgBookCover)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvAuthor = findViewById<TextView>(R.id.tvAuthor)
        val tvPublisher = findViewById<TextView>(R.id.tvPublisher)
        val tvCategory = findViewById<TextView>(R.id.tvCategory)
        val tvIBPN = findViewById<TextView>(R.id.tvIBPN)
        val tvYear = findViewById<TextView>(R.id.tvYear)
        val tvPages = findViewById<TextView>(R.id.tvPages)
        val tvStock = findViewById<TextView>(R.id.tvStock)
        val tvPrice = findViewById<TextView>(R.id.tvPrice)
        val tvDescription = findViewById<TextView>(R.id.tvDescription)

        if (book != null) {
            tvTitle.text = book.TenSach
            tvAuthor.text = book.TacGia
            tvPublisher.text = book.NXB
            tvCategory.text = categoryDao.getCategoryNameById(book.MaTL)
            tvIBPN.text = book.ISBN
            tvYear.text = book.NamXB.toString()
            tvPages.text = book.SoTrang.toString()
            tvStock.text = book.SoLuongTon.toString()
            tvPrice.text = book.GiaBan.toString()
            tvDescription.text = book.MoTa
            imgBookCover.setImageResource(R.drawable.book_cover)
        } else {
            tvTitle.text = "Không tìm thấy sách"
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
//                editBook()
                true
            }
            R.id.menu_delete -> {
                deleteBook()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteBook() {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận")
            .setMessage("Bạn có chắc chắn muốn xóa sách này không?")
            .setPositiveButton("Có") { _, _ ->
//                databaseHelper.deleteBook(bookId)
                Toast.makeText(this, "Đã xóa sách thành công", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("Không") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}