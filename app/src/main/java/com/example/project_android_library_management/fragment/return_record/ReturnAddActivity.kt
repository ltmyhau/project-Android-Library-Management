package com.example.project_android_library_management.fragment.return_record

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.BookReturnAdapter
import com.example.project_android_library_management.dao.BookDao
import com.example.project_android_library_management.dao.BorrowDetailDao
import com.example.project_android_library_management.dao.BorrowRecordDao
import com.example.project_android_library_management.dao.LibrarianDao
import com.example.project_android_library_management.dao.ReaderDao
import com.example.project_android_library_management.dao.ReturnDetailDao
import com.example.project_android_library_management.dao.ReturnRecordDao
import com.example.project_android_library_management.fragment.order_book.OrderDetailActivity
import com.example.project_android_library_management.fragment.return_record.ReturnUpdateActivity.Companion
import com.example.project_android_library_management.model.Book
import com.example.project_android_library_management.model.BorrowDetail
import com.example.project_android_library_management.model.BorrowRecord
import com.example.project_android_library_management.model.ReturnDetail
import com.example.project_android_library_management.model.ReturnRecord
import com.example.project_android_library_management.search.SearchBookActivity
import com.example.project_android_library_management.search.SearchBorrowActivity
import com.example.project_android_library_management.search.SearchLibrarianActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class ReturnAddActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var readerDao: ReaderDao
    private lateinit var librarianDao: LibrarianDao
    private lateinit var returnRecordDao: ReturnRecordDao
    private lateinit var returnDetailDao: ReturnDetailDao
    private lateinit var borrowRecordDao: BorrowRecordDao
    private lateinit var borrowDetailDao: BorrowDetailDao
    private lateinit var bookDao: BookDao

    private var maPT: String = ""
    private var borrowId: String = ""
    private var readerId: String = ""
    private var librarianId: String = ""
    private var expectedReturnDate: String = ""
    private var actualReturnDate: String = ""
    private var totalCompensation: Double = 0.0
    private var overdueDays: Double = 0.0
    private var bookReturns: ArrayList<ReturnDetail> = arrayListOf()
    private var bookBorrows: ArrayList<BorrowDetail> = arrayListOf()
    private var bookList: ArrayList<Book> = ArrayList()

    private lateinit var edtReturnId: TextInputEditText
    private lateinit var edtLibrarianName: TextInputEditText
    private lateinit var edtReaderName: TextInputEditText
    private lateinit var edtBorowId: TextInputEditText
    private lateinit var edtActualReturnDate: TextInputEditText
    private lateinit var edtExpectedReturnDate: TextInputEditText
    private lateinit var edtFines: TextInputEditText
    private lateinit var rcvBooks: RecyclerView
    private lateinit var booksLayout: LinearLayout
    private lateinit var finesLayout: LinearLayout
    private lateinit var finesTableLayout: TableLayout
    private lateinit var finesTableRow: TableRow
    private lateinit var btnAddBook: ImageButton

    companion object {
        private const val REQUEST_CODE_LIBRARIAN_ID = 1
        private const val REQUEST_CODE_BOOK_ID = 2
        private const val REQUEST_CODE_BORROW_ID = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_edit)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        databaseHelper = DatabaseHelper(this)
        readerDao = ReaderDao(databaseHelper)
        librarianDao = LibrarianDao(databaseHelper)
        returnRecordDao = ReturnRecordDao(databaseHelper)
        returnDetailDao = ReturnDetailDao(databaseHelper)
        borrowRecordDao = BorrowRecordDao(databaseHelper)
        borrowDetailDao = BorrowDetailDao(databaseHelper)
        bookDao = BookDao(databaseHelper)

        edtReturnId = findViewById(R.id.edtReturnId)
        edtReaderName = findViewById(R.id.edtReaderName)
        edtLibrarianName = findViewById(R.id.edtLibrarianName)
        edtBorowId = findViewById(R.id.edtBorowId)
        edtActualReturnDate = findViewById(R.id.edtActualReturnDate)
        edtExpectedReturnDate = findViewById(R.id.edtExpectedReturnDate)
        edtFines = findViewById(R.id.edtFines)
        rcvBooks = findViewById(R.id.rcvBooks)
        booksLayout = findViewById(R.id.booksLayout)
        finesLayout = findViewById(R.id.finesLayout)
        finesTableLayout = findViewById(R.id.finesTableLayout)
        finesTableRow = findViewById(R.id.finesTableRow)

        finesLayout.visibility = View.GONE
        booksLayout.visibility = View.GONE

        maPT = returnRecordDao.generateNewId()
        edtReturnId.setText(maPT)

        edtLibrarianName.setOnClickListener {
            val intent = Intent(this, SearchLibrarianActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_LIBRARIAN_ID)
        }

        val borrowList = borrowRecordDao.getAllBorrowRecord()
        val returnList = returnRecordDao.getAllReturnRecord()
        val returnedMaPMs = returnList.map { it.MaPM }.toSet()
        val borrowListNotReturned = ArrayList(borrowList.filter { it.MaPM !in returnedMaPMs })

        edtBorowId.setOnClickListener {
            val intent = Intent(this, SearchBorrowActivity::class.java)
            intent.putExtra("SOURCE", "BorrowRecordNotReturned")
            intent.putExtra("BORROW_LIST", borrowListNotReturned)
            startActivityForResult(intent, REQUEST_CODE_BORROW_ID)
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        actualReturnDate = sdf.format(Date())
        edtActualReturnDate.setText(actualReturnDate)

        val actualReturnDateLayout = findViewById<TextInputLayout>(R.id.actualReturnDateLayout)
        actualReturnDateLayout.setStartIconOnClickListener {
            showDatePickerDialog(edtActualReturnDate)
        }

        btnAddBook = findViewById(R.id.btnAddBook)
        btnAddBook.setOnClickListener {
            val intent = Intent(this, SearchBookActivity::class.java)
            intent.putExtra("SOURCE", "BookList")
            intent.putExtra("BOOK_LIST", bookList)
            startActivityForResult(intent, REQUEST_CODE_BOOK_ID)
        }

        val btnEdit = findViewById<AppCompatButton>(R.id.btnEdit)
        btnEdit.text = "Thêm"
        btnEdit.setOnClickListener {
            addReturnRecord()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_LIBRARIAN_ID && resultCode == RESULT_OK) {
            librarianId = data?.getStringExtra("LIBRARIAN_ID") ?: ""
            if (librarianId != null && librarianId.isNotEmpty()) {
                val librarian = librarianDao.getLibrarianById(librarianId)
                edtLibrarianName.setText(librarian?.HoTen ?: "")
                edtLibrarianName.error = null
            }
        } else if (requestCode == REQUEST_CODE_BORROW_ID && resultCode == RESULT_OK) {
            borrowId = data?.getStringExtra("BORROW_ID") ?: ""
            if (borrowId.isNotEmpty()) {
                bookBorrows = borrowDetailDao.getBorrowDetailsById(borrowId)
                edtBorowId.error = null
                edtBorowId.setText(borrowId)
                bookReturns = ArrayList()
                for (bookBorrow in bookBorrows) {
                    val bookReturn = ReturnDetail(maPT, bookBorrow.MaSach, bookBorrow.SoLuong)
                    if (bookReturn != null) {
                        bookReturns.add(bookReturn)
                    }
                }
                booksLayout.visibility = View.VISIBLE
                loadBookReturns(bookReturns)

                val borrowRecordDao = BorrowRecordDao(databaseHelper)
                val borrowRecord = borrowRecordDao.getBorrowRecordById(borrowId)
                borrowRecord?.let {
                    expectedReturnDate =
                        addDaysToDate(borrowRecord.NgayMuon, borrowRecord.SoNgayMuon)
                    edtExpectedReturnDate.setText(expectedReturnDate)
                    readerId = borrowRecord.MaDG
                    val reader = readerDao.getReaderById(readerId)
                    edtReaderName.setText(reader?.HoTen ?: "")
                }

                finesLayout.visibility = if (checkFines()) View.VISIBLE else View.GONE
                checkDifferenceAndDisplayButton()
            }
        } else if (requestCode == REQUEST_CODE_BOOK_ID && resultCode == RESULT_OK) {
            val bookId = data?.getStringExtra("BOOK_ID") ?: ""
            if (bookId.isNotEmpty()) {
                var bookExists = false
                for (bookDetail in bookReturns) {
                    if (bookDetail.MaSach == bookId) {
                        bookDetail.SoLuong += 1
                        bookExists = true
                        break
                    }
                }
                if (!bookExists) {
                    val newBookDetail = ReturnDetail(maPT, bookId, 1)
                    bookReturns.add(newBookDetail)
                }
                loadBookReturns(bookReturns)
                checkDifferenceAndDisplayButton()
                finesLayout.visibility = if (checkFines()) View.VISIBLE else View.GONE
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
                actualReturnDate = "${year}-${formattedMonth}-${formattedDay}"
                textView.text = actualReturnDate
                calculateOverdueFine()
                totalCompensation += overdueDays
                edtFines.setText(String.format("%.0f", totalCompensation))
                finesLayout.visibility = if (checkFines()) View.VISIBLE else View.GONE
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun loadBookReturns(bookReturns: ArrayList<ReturnDetail>) {
        if (bookReturns != null) {
            rcvBooks.layoutManager = LinearLayoutManager(this)
            val bookReturnAdapter = BookReturnAdapter(bookReturns, borrowId)

            bookReturnAdapter.setOnQuantityChangeListener(object :
                BookReturnAdapter.OnQuantityChangeListener {
                override fun onQuantityChange() {
                    finesLayout.visibility = if (checkFines()) View.VISIBLE else View.GONE
                    checkDifferenceAndDisplayButton()
                }
            })

            rcvBooks.adapter = bookReturnAdapter
        } else {
            Toast.makeText(this, "Không tìm thấy chi tiết phiếu mượn", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addDaysToDate(dateStr: String, daysToAdd: Int): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateStr)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_MONTH, daysToAdd)
        return dateFormat.format(calendar.time)
    }

    private fun calculateDaysBetweenDates(startDateStr: String, endDateStr: String): Int {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val startDate = dateFormat.parse(startDateStr)
        val endDate = dateFormat.parse(endDateStr)
        val diffInMillis = endDate.time - startDate.time
        return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS).toInt()
    }

    private fun calculateOverdueFine() {
        val daysOverdue = calculateDaysBetweenDates(expectedReturnDate, actualReturnDate)
        if (daysOverdue > 0) {
            overdueDays = daysOverdue * 2000.0
        } else {
            overdueDays = 0.0
        }
    }

    fun calculateCompensation(
        borrowDetails: List<BorrowDetail>,
        returnDetails: List<ReturnDetail>
    ): Double {
        val borrowMap = borrowDetails.groupBy { it.MaSach }
            .mapValues { entry -> entry.value.sumOf { it.SoLuong } }

        val returnMap = returnDetails.groupBy { it.MaSach }
            .mapValues { entry -> entry.value.sumOf { it.SoLuong } }

        var totalCompensation = 0.0
        val processedBooks = mutableSetOf<String>()

        for ((bookId, borrowedQuantity) in borrowMap) {
            val returnedQuantity = returnMap.getOrDefault(bookId, 0)
            if (returnedQuantity < borrowedQuantity) {
                val missingQuantity = borrowedQuantity - returnedQuantity
                val book = bookDao.getBookById(bookId)
                if (book != null) {
                    totalCompensation += missingQuantity * book.GiaBan

                    if (!processedBooks.contains(bookId)) {
                        val penalty = String.format("%.0f", missingQuantity * book.GiaBan)
                        addTableRow(
                            finesTableLayout,
                            "Mất ${missingQuantity} sách ${book.TenSach}",
                            penalty
                        )
                        processedBooks.add(bookId)
                    }
                }
            }
        }
        return totalCompensation
    }

    private fun checkFines(): Boolean {
        var isFines = false
        totalCompensation = 0.0
        removeOtherTableRows(finesTableLayout, finesTableRow)

        calculateOverdueFine()

        val daysOverdue = calculateDaysBetweenDates(expectedReturnDate, actualReturnDate)
        if (daysOverdue > 0) {
            totalCompensation += overdueDays
            val overdueFine = String.format("%.0f", overdueDays)
            addTableRow(finesTableLayout, "Quá hạn ${daysOverdue} ngày", overdueFine)
            isFines = true
        }

        totalCompensation += calculateCompensation(bookBorrows, bookReturns)
        if (totalCompensation > 0) {
            edtFines.setText(String.format("%.0f", totalCompensation))
            isFines = true
        } else {
            edtFines.setText("0")
        }

        return isFines
    }

    private fun checkDifferenceAndDisplayButton() {
        val booksNotReturned = bookBorrows.filter { borrow ->
            bookReturns.none { returnBook -> returnBook.MaSach == borrow.MaSach }
        }

        if (booksNotReturned.isNotEmpty()) {
            btnAddBook.visibility = View.VISIBLE

            val bookIdList = booksNotReturned.map { it.MaSach }

            bookList = bookIdList.mapNotNull { maSach ->
                bookDao.getBookById(maSach)
            }.toCollection(ArrayList())

        } else {
            btnAddBook.visibility = View.GONE
        }

    }

    private fun addTableRow(tableLayout: TableLayout, violation: String, penalty: String) {
        val tableRow = TableRow(tableLayout.context)

        val tableRowParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        tableRow.layoutParams = tableRowParams
        tableRow.setBackgroundResource(R.drawable.table_border)

        val customFont = ResourcesCompat.getFont(tableLayout.context, R.font.oswald)

        val textView1 = TextView(tableLayout.context).apply {
            layoutParams = TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                2f
            )
            setBackgroundResource(R.drawable.right_line)
            setPadding(32, 8, 8, 16)
            text = violation
            textSize = 16f
            typeface = customFont
            setTypeface(typeface, Typeface.NORMAL)
        }

        val textView2 = TextView(tableLayout.context).apply {
            layoutParams = TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
            )
            setPadding(8, 8, 8, 16)
            text = penalty
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            textSize = 16f
            typeface = customFont
            setTypeface(typeface, Typeface.NORMAL)
        }

        tableRow.addView(textView1)
        tableRow.addView(textView2)

        tableLayout.addView(tableRow)
    }

    fun removeOtherTableRows(tableLayout: TableLayout, headerRow: TableRow) {
        for (i in tableLayout.childCount - 1 downTo 0) {
            val child = tableLayout.getChildAt(i)
            if (child is TableRow && child != headerRow) {
                tableLayout.removeViewAt(i)
            }
        }
    }

    private fun addReturnRecord() {
        if (validateFields()) {
            val returnDate = edtActualReturnDate.text.toString()
            val fines = edtFines.text.toString().toDouble()
            val borrowId = edtBorowId.text.toString()

            val returnRecord =
                ReturnRecord(maPT, returnDate, fines, borrowId, librarianId, readerId)
            val rowsAffected = returnRecordDao.insert(returnRecord)
            if (rowsAffected > 0 && addBookReturn()) {
                Toast.makeText(this, "Thêm phiếu trả thành công", Toast.LENGTH_SHORT)
                    .show()
//                val resultIntent = Intent()
//                resultIntent.putExtra("RETURN_ID", maPT)
//                setResult(RESULT_OK, resultIntent)
//                finish()
                val intent = Intent(this, ReturnDetailActivity::class.java)
                intent.putExtra("RETURN_ID", maPT)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Thêm phiếu trả thất bại", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun addBookReturn(): Boolean {
        var allSuccess = true

        if (allSuccess) {
            for (returnDetail in bookReturns) {
                val rowsAffected = returnDetailDao.insert(returnDetail)
                if (rowsAffected <= 0) {
                    allSuccess = false
                    break
                }
            }
        }
        return allSuccess
    }

    private fun validateFields(): Boolean {
        if (edtLibrarianName.text.isNullOrEmpty() || edtLibrarianName.text.toString() == "Thủ thư") {
            edtLibrarianName.error = ""
            Toast.makeText(this, "Vui lòng chọn thủ thư", Toast.LENGTH_SHORT).show()
            return false
        }
        if (edtBorowId.text.isNullOrEmpty() || edtBorowId.text.toString() == "Mã phiếu mượn") {
            edtBorowId.error = ""
            Toast.makeText(this, "Vui lòng chọn phiếu mượn", Toast.LENGTH_SHORT).show()
            return false
        }
        if (edtActualReturnDate.text.isNullOrEmpty()) {
            edtActualReturnDate.error = "Ngày trả (yyyy-MM-dd) không được để trống"
            edtActualReturnDate.requestFocus()
            return false
        } else if (!validateDate(edtActualReturnDate.text.toString())) {
            edtActualReturnDate.error = "Ngày trả (yyyy-MM-dd) không hợp lệ"
            edtActualReturnDate.requestFocus()
            return false
        }
        if (bookReturns.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn sách cần trả", Toast.LENGTH_SHORT).show()
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