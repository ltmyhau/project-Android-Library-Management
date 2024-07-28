package com.example.project_android_library_management.fragment.order_book

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.BookOrderAdapter
import com.example.project_android_library_management.dao.BookDao
import com.example.project_android_library_management.dao.LibrarianDao
import com.example.project_android_library_management.dao.OrderBookDao
import com.example.project_android_library_management.dao.OrderDetailDao
import com.example.project_android_library_management.dao.PublisherDao
import com.example.project_android_library_management.model.Book
import com.example.project_android_library_management.model.BorrowRecord
import com.example.project_android_library_management.model.OrderBook
import com.example.project_android_library_management.model.OrderDetail
import com.example.project_android_library_management.search.SearchBookActivity
import com.example.project_android_library_management.search.SearchLibrarianActivity
import com.example.project_android_library_management.search.SearchPublisherActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OrderUpdateActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var bookDao: BookDao
    private lateinit var publisherDao: PublisherDao
    private lateinit var librarianDao: LibrarianDao
    private lateinit var orderBookDao: OrderBookDao
    private lateinit var orderDetailDao: OrderDetailDao
    private lateinit var bookOrderAdapter: BookOrderAdapter
    private lateinit var bookOrders: ArrayList<OrderDetail>

    private var maPD: String = ""
    private var publisherId: String = ""
    private var librarianId: String = ""
    private var bookListByPublisher: ArrayList<Book> = ArrayList()

    private lateinit var edtOrderId: TextInputEditText
    private lateinit var edtPublisher: TextInputEditText
    private lateinit var edtLibrarianName: TextInputEditText
    private lateinit var edtOrderDate: TextInputEditText
    private lateinit var edtNotes: TextInputEditText
    private lateinit var rcvBooks: RecyclerView

    companion object {
        private const val REQUEST_CODE_PUBLISHER_ID = 1
        private const val REQUEST_CODE_LIBRARIAN_ID = 2
        private const val REQUEST_CODE_BOOK_ID = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_edit)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        maPD = intent.getStringExtra("ORDER_ID") ?: ""

        databaseHelper = DatabaseHelper(this)
        bookDao = BookDao(databaseHelper)
        publisherDao = PublisherDao(databaseHelper)
        librarianDao = LibrarianDao(databaseHelper)
        orderBookDao = OrderBookDao(databaseHelper)
        orderDetailDao = OrderDetailDao(databaseHelper)

        edtOrderId = findViewById(R.id.edtOrderId)
        edtPublisher = findViewById(R.id.edtPublisher)
        edtLibrarianName = findViewById(R.id.edtLibrarianName)
        edtOrderDate = findViewById(R.id.edtOrderDate)
        edtNotes = findViewById(R.id.edtNotes)
        rcvBooks = findViewById(R.id.rcvBooks)

        loadOrderDetails(maPD)

        bookOrders = orderDetailDao.getOrderDetailsById(maPD)
        loadBookOrders(bookOrders)

        edtPublisher.setOnClickListener {
            val intent = Intent(this, SearchPublisherActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_PUBLISHER_ID)
        }

        edtLibrarianName.setOnClickListener {
            val intent = Intent(this, SearchLibrarianActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_LIBRARIAN_ID)
        }

        val orderDateLayout = findViewById<TextInputLayout>(R.id.orderDateLayout)
        orderDateLayout.setStartIconOnClickListener {
            showDatePickerDialog(edtOrderDate)
        }

        bookListByPublisher = bookDao.getBooksByPublisherId(publisherId)
        val btnAddBook = findViewById<ImageButton>(R.id.btnAddBook)
        btnAddBook.setOnClickListener {
            val intent = Intent(this, SearchBookActivity::class.java)
            intent.putExtra("SOURCE", "BookList")
            intent.putExtra("BOOK_LIST", bookListByPublisher)
            startActivityForResult(intent, REQUEST_CODE_BOOK_ID)
        }

        val btnEdit = findViewById<AppCompatButton>(R.id.btnEdit)
        btnEdit.text = "Lưu thông tin"
        btnEdit.setOnClickListener {
            saveOrderBook()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PUBLISHER_ID && resultCode == RESULT_OK) {
            publisherId = data?.getStringExtra("PUBLISHER_ID") ?: ""
            if (publisherId != null && publisherId.isNotEmpty()) {
                val publisher = publisherDao.getPublisherById(publisherId)
                edtPublisher.setText(publisher?.TenNXB ?: "")
                bookListByPublisher = bookDao.getBooksByPublisherId(publisherId)
                edtPublisher.error = null
                bookOrders = ArrayList<OrderDetail>()
                loadBookOrders(bookOrders)
            }
        } else if (requestCode == REQUEST_CODE_LIBRARIAN_ID && resultCode == RESULT_OK) {
            librarianId = data?.getStringExtra("LIBRARIAN_ID") ?: ""
            if (librarianId != null && librarianId.isNotEmpty()) {
                val librarian = librarianDao.getLibrarianById(librarianId)
                edtLibrarianName.setText(librarian?.HoTen ?: "")
                edtLibrarianName.error = null
            }
        } else if (requestCode == REQUEST_CODE_BOOK_ID && resultCode == RESULT_OK) {
            val bookId = data?.getStringExtra("BOOK_ID") ?: ""
            if (bookId.isNotEmpty()) {
                var bookExists = false
                for (bookDetail in bookOrders) {
                    if (bookDetail.MaSach == bookId) {
                        bookDetail.SoLuong += 1
                        bookExists = true
                        break
                    }
                }
                if (!bookExists) {
                    val newBookDetail = OrderDetail(maPD, bookId, 1)
                    bookOrders.add(newBookDetail)
                }
                loadBookOrders(bookOrders)
            }
        }
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

    private fun loadOrderDetails(maPM: String) {
        val orderBook = orderBookDao.getOrderBookById(maPM)

        if (orderBook != null) {
            edtOrderId.setText(orderBook.MaPD)
            edtOrderDate.setText(orderBook.NgayDat)
            edtNotes.setText(orderBook.GhiChu)

            publisherId = orderBook.MaNXB
            val publisherDao = PublisherDao(databaseHelper)
            val publisher = publisherDao.getPublisherById(publisherId)

            librarianId = orderBook.MaTT
            val libraryDao = LibrarianDao(databaseHelper)
            val librarian = libraryDao.getLibrarianById(librarianId)

            publisher?.let { edtPublisher.setText(it.TenNXB) }
            librarian?.let { edtLibrarianName.setText(it.HoTen) }
        } else {
            Toast.makeText(this, "Không tìm thấy phiếu đặt", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadBookOrders(bookOrders: ArrayList<OrderDetail>) {
        if (bookOrders != null) {
            rcvBooks.layoutManager = LinearLayoutManager(this)
            bookOrderAdapter = BookOrderAdapter(bookOrders)
            rcvBooks.adapter = bookOrderAdapter
        } else {
            Toast.makeText(this, "Không tìm thấy chi tiết phiếu đặt", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveOrderBook() {
        val orderDate = edtOrderDate.text.toString()
        val notes = edtNotes.text.toString()

        if (validateFields()) {
            val orderBook = OrderBook(maPD, orderDate, notes, librarianId, publisherId)
            val rowsAffected = orderBookDao.update(orderBook)
            if (rowsAffected > 0 && saveOrderDetail()) {
                Toast.makeText(this, "Cập nhật thông tin phiếu đặt thành công", Toast.LENGTH_SHORT)
                    .show()
                val resultIntent = Intent()
                resultIntent.putExtra("ORDER_ID", maPD)
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Cập nhật thông tin phiếu đặt thất bại", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun saveOrderDetail(): Boolean {
        var allSuccess = true

        try {
            val rowsDeleted = orderDetailDao.delete(maPD)
            if (rowsDeleted <= 0) {
                allSuccess = false
            }
        } catch (e: Exception) {
            allSuccess = false
        }

        if (allSuccess) {
            for (bookOrder in bookOrders) {
                val rowsAffected = orderDetailDao.insert(bookOrder)
                if (rowsAffected <= 0) {
                    allSuccess = false
                    break
                }
            }
        }
        return allSuccess
    }

    private fun validateFields(): Boolean {
        if (edtPublisher.text.isNullOrEmpty() || edtPublisher.text.toString() == "Độc giả") {
            edtPublisher.error = ""
            Toast.makeText(this, "Vui lòng chọn nhà xuất bản", Toast.LENGTH_SHORT).show()
            return false
        }
        if (edtLibrarianName.text.isNullOrEmpty() || edtLibrarianName.text.toString() == "Thủ thư") {
            edtLibrarianName.error = ""
            Toast.makeText(this, "Vui lòng chọn thủ thư", Toast.LENGTH_SHORT).show()
            return false
        }
        if (edtOrderDate.text.isNullOrEmpty()) {
            edtOrderDate.error = "Ngày đặt (yyyy-MM-dd) không được để trống"
            edtOrderDate.requestFocus()
            return false
        } else if (!validateDate(edtOrderDate.text.toString())) {
            edtOrderDate.error = "Ngày đặt (yyyy-MM-dd) không hợp lệ"
            edtOrderDate.requestFocus()
            return false
        }
        if (bookOrders.isEmpty()) {
            Toast.makeText(this, "Vui lòng đặt ít mượn 1 quyển sách", Toast.LENGTH_SHORT).show()
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
}