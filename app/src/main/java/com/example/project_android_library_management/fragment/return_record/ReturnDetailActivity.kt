package com.example.project_android_library_management.fragment.return_record

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.BookAdapter
import com.example.project_android_library_management.dao.BookDao
import com.example.project_android_library_management.dao.BorrowDetailDao
import com.example.project_android_library_management.dao.BorrowRecordDao
import com.example.project_android_library_management.dao.LibrarianDao
import com.example.project_android_library_management.dao.ReaderDao
import com.example.project_android_library_management.dao.ReturnDetailDao
import com.example.project_android_library_management.dao.ReturnRecordDao
import com.example.project_android_library_management.model.BorrowDetail
import com.example.project_android_library_management.model.ReturnDetail
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class ReturnDetailActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var returnRecordDao: ReturnRecordDao
    private lateinit var returnDetailDao: ReturnDetailDao
    private lateinit var borrowDetailDao: BorrowDetailDao
    private lateinit var bookDao: BookDao

    private var maPT: String = ""
    private var maPM: String = ""
    private var expectedReturnDate: String = ""
    private var actualReturnDate: String = ""
    private var totalCompensation: Double = 0.0
    private var bookReturns: ArrayList<ReturnDetail> = arrayListOf()
    private var bookBorrows: ArrayList<BorrowDetail> = arrayListOf()

    private lateinit var tvReturnRecordID: TextView
    private lateinit var tvReaderName: TextView
    private lateinit var tvLibrarianName: TextView
    private lateinit var tvBorrowDate: TextView
    private lateinit var tvReturnDate: TextView
    private lateinit var tvDeposit: TextView
    private lateinit var tvFines: TextView
    private lateinit var tvRemains: TextView
    private lateinit var rcvBooks: RecyclerView
    private lateinit var finesLayout: LinearLayout
    private lateinit var finesTableLayout: TableLayout
    private lateinit var finesTableRow: TableRow

    companion object {
        private const val REQUEST_CODE_UPDATE_RETURN = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        maPT = intent.getStringExtra("RETURN_ID") ?: ""
        maPM = intent.getStringExtra("BORROW_ID") ?: ""

        databaseHelper = DatabaseHelper(this)
        returnRecordDao = ReturnRecordDao(databaseHelper)
        returnDetailDao = ReturnDetailDao(databaseHelper)
        borrowDetailDao = BorrowDetailDao(databaseHelper)
        bookDao = BookDao(databaseHelper)

        tvReturnRecordID = findViewById(R.id.tvRecordID)
        tvLibrarianName = findViewById(R.id.tvLibrarianName)
        tvReaderName = findViewById(R.id.tvReaderName)
        tvBorrowDate = findViewById(R.id.tvBorrowDate)
        tvReturnDate = findViewById(R.id.tvReturnDate)
        tvDeposit = findViewById(R.id.tvDeposit)
        tvFines = findViewById(R.id.tvFines)
        tvRemains = findViewById(R.id.tvRemains)
        rcvBooks = findViewById(R.id.rcvBooks)
        finesLayout = findViewById(R.id.finesLayout)
        finesTableLayout = findViewById(R.id.finesTableLayout)
        finesTableRow = findViewById(R.id.finesTableRow)

        if (maPT.isEmpty()) {
            maPT = returnRecordDao.getReturnRecordByBorrowId(maPM)?.MaPT ?: ""
        }

        loadReturnDetails(maPT)
        loadBookReturns(maPT)

        finesLayout.visibility = if (checkFines()) View.VISIBLE else View.GONE

        Log.e("SACHMUON", bookBorrows.toString())
        Log.e("SACHTRA", bookReturns.toString())
        Log.e("NGAYQUAHAN", calculateDaysBetweenDates(expectedReturnDate, actualReturnDate).toString())
        Log.e("NGAYMUON", tvBorrowDate.text.toString())
        Log.e("NGAYTRATHUCTE", tvReturnDate.text.toString())
        Log.e("NGAYTRADUKIEN", expectedReturnDate)
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
                editReturnRecord(maPT)
                true
            }

            R.id.menu_delete -> {
                deleteReturnRecord()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UPDATE_RETURN && resultCode == RESULT_OK) {
            loadReturnDetails(maPT)
            loadBookReturns(maPT)
            finesLayout.visibility = if (checkFines()) View.VISIBLE else View.GONE
        }
    }

    private fun loadReturnDetails(maPT: String) {
        val returnRecord = returnRecordDao.getReturnRecordById(maPT)

        if (returnRecord != null) {
            tvReturnRecordID.text = returnRecord.MaPT
            tvReturnDate.text = returnRecord.NgayTra
            tvFines.text = String.format("%.0f", returnRecord.TienPhat)

            actualReturnDate = returnRecord.NgayTra

            val readerDao = ReaderDao(databaseHelper)
            val reader = readerDao.getReaderById(returnRecord.MaDG)
            val libraryDao = LibrarianDao(databaseHelper)
            val librarian = libraryDao.getLibrarianById(returnRecord.MaTT)
            val borrowRecordDao = BorrowRecordDao(databaseHelper)
            val borrowRecord = borrowRecordDao.getBorrowRecordById(returnRecord.MaPM)

            bookBorrows = borrowDetailDao.getBorrowDetailsById(returnRecord.MaPM)

            reader?.let { tvReaderName.text = reader.HoTen }
            librarian?.let { tvLibrarianName.text = librarian.HoTen }
            borrowRecord?.let {
                tvBorrowDate.text = borrowRecord.NgayMuon
                tvDeposit.text = String.format("%.0f", borrowRecord.TienCoc)
                tvRemains.text = String.format("%.0f", borrowRecord.TienCoc - returnRecord.TienPhat)
                expectedReturnDate = addDaysToDate(borrowRecord.NgayMuon, borrowRecord.SoNgayMuon)
            }
        } else {
            Toast.makeText(this, "Không tìm thấy phiếu trả", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadBookReturns(maPT: String) {
        bookReturns = returnDetailDao.getReturnDetailById(maPT)

        if (bookReturns != null) {
            rcvBooks.layoutManager = LinearLayoutManager(this)
            val bookAdapter = BookAdapter(null, null, bookReturns, null, null)
            rcvBooks.adapter = bookAdapter
        } else {
            Toast.makeText(this, "Không tìm thấy chi tiết phiếu trả", Toast.LENGTH_SHORT).show()
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

    private fun calculateOverdueFine(): Double {
        val daysOverdue = calculateDaysBetweenDates(expectedReturnDate, actualReturnDate)
        return if (daysOverdue > 0) {
            daysOverdue * 2000.0
        } else {
            0.0
        }
    }

    fun calculateCompensation(borrowDetails: List<BorrowDetail>, returnDetails: List<ReturnDetail>): Double {
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
                        addTableRow(finesTableLayout, "Mất ${missingQuantity} sách ${book.TenSach}", penalty)
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

        val daysOverdue = calculateDaysBetweenDates(expectedReturnDate, actualReturnDate)
        if (daysOverdue > 0) {
            totalCompensation += calculateOverdueFine()
            val overdueFine = String.format("%.0f", calculateOverdueFine())
            addTableRow(finesTableLayout, "Quá hạn ${daysOverdue} ngày", overdueFine)
            isFines = true
        }

        totalCompensation = calculateCompensation(bookBorrows, bookReturns)
        if (totalCompensation > 0) {
            isFines = true
        }

        return isFines
    }

    fun addTableRow(tableLayout: TableLayout, violation: String, penalty: String) {
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

    private fun editReturnRecord(maPM: String) {
        val intent = Intent(this, ReturnUpdateActivity::class.java)
        intent.putExtra("RETURN_ID", maPM)
        startActivityForResult(intent, REQUEST_CODE_UPDATE_RETURN)
    }

    private fun deleteReturnRecord() {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận")
            .setMessage("Bạn có chắc chắn muốn xóa phiếu trả này không?")
            .setPositiveButton("Có") { _, _ ->
                val rowsAffected = returnRecordDao.delete(maPT)
                if (rowsAffected > 0) {
                    Toast.makeText(this, "Đã xóa phiếu trả thành công", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Xóa phiếu trả thất bại", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
            .setNegativeButton("Không") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}