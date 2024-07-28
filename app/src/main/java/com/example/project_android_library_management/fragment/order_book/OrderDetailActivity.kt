package com.example.project_android_library_management.fragment.order_book

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.BookAdapter
import com.example.project_android_library_management.dao.LibrarianDao
import com.example.project_android_library_management.dao.OrderBookDao
import com.example.project_android_library_management.dao.OrderDetailDao
import com.example.project_android_library_management.dao.PublisherDao
import com.example.project_android_library_management.dao.ReaderDao

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var orderBookDao: OrderBookDao
    private lateinit var orderDetailDao: OrderDetailDao

    private var maPD: String = ""

    private lateinit var tvOrderID: TextView
    private lateinit var tvPublisher: TextView
    private lateinit var tvLibrarianName: TextView
    private lateinit var tvOrderDate: TextView
    private lateinit var tvNotes: TextView
    private lateinit var rcvBooks: RecyclerView

    companion object {
        private const val REQUEST_CODE_ORDER_LIST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        maPD = intent.getStringExtra("ORDER_ID") ?: ""

        databaseHelper = DatabaseHelper(this)
        orderBookDao = OrderBookDao(databaseHelper)
        orderDetailDao = OrderDetailDao(databaseHelper)

        tvOrderID = findViewById(R.id.tvOrderID)
        tvPublisher = findViewById(R.id.tvPublisher)
        tvLibrarianName = findViewById(R.id.tvLibrarianName)
        tvOrderDate = findViewById(R.id.tvOrderDate)
        tvNotes = findViewById(R.id.tvNotes)
        rcvBooks = findViewById(R.id.rcvBooks)

        loadOrderBooks(maPD)
        loadOrderDetails(maPD)
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
//                editBorrowRecord(maPD)
                true
            }

            R.id.menu_delete -> {
//                deleteBorrowRecord()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadOrderBooks(maPD: String) {
        val orderBook = orderBookDao.getOrderBookById(maPD)

        if (orderBook != null) {
            tvOrderID.text = orderBook.MaPD
            tvOrderDate.text = orderBook.NgayDat
            tvNotes.text = orderBook.GhiChu

            val publisherDao = PublisherDao(databaseHelper)
            val publisher = publisherDao.getPublisherById(orderBook.MaNXB)
            val libraryDao = LibrarianDao(databaseHelper)
            val librarian = libraryDao.getLibrarianById(orderBook.MaTT)

            publisher?.let { tvPublisher.text = publisher.TenNXB }
            librarian?.let { tvLibrarianName.text = librarian.HoTen }
        } else {
            Toast.makeText(this, "Không tìm thấy phiếu đặt", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadOrderDetails(maPD: String) {
        val orderDetails = orderDetailDao.getOrderDetailsById(maPD)

        if (orderDetails != null) {
            rcvBooks.layoutManager = LinearLayoutManager(this)
            val bookAdapter = BookAdapter(null, null, null, orderDetails, null)
            rcvBooks.adapter = bookAdapter
        } else {
            Toast.makeText(this, "Không tìm thấy chi tiết phiếu mượn", Toast.LENGTH_SHORT).show()
        }
    }
}