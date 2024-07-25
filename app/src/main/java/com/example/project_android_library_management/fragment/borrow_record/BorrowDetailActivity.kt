package com.example.project_android_library_management.fragment.borrow_record

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.BookAdapter
import com.example.project_android_library_management.adapter.BookBorrowAdapter
import com.example.project_android_library_management.dao.BorrowDetailDao
import com.example.project_android_library_management.dao.BorrowRecordDao
import com.example.project_android_library_management.dao.LibrarianDao
import com.example.project_android_library_management.dao.ReaderDao
import com.example.project_android_library_management.fragment.reader.ReaderUpdateActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BorrowDetailActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var borrowRecordDao: BorrowRecordDao
    private lateinit var borrowDetailDao: BorrowDetailDao
    private var maPM: String = ""

    private lateinit var tvBorrowRecordID: TextView
    private lateinit var tvReaderName: TextView
    private lateinit var tvLibrarianName: TextView
    private lateinit var tvTimeBorrow: TextView
    private lateinit var tvBorrowDate: TextView
    private lateinit var tvReturnDate: TextView
    private lateinit var tvDeposit: TextView
    private lateinit var rcvBooks: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_borrow_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        maPM = intent.getStringExtra("BORROW_ID") ?: ""

        databaseHelper = DatabaseHelper(this)
        borrowRecordDao = BorrowRecordDao(databaseHelper)
        borrowDetailDao = BorrowDetailDao(databaseHelper)

        tvBorrowRecordID = findViewById(R.id.tvBorrowRecordID)
        tvReaderName = findViewById(R.id.tvReaderName)
        tvLibrarianName = findViewById(R.id.tvLibrarianName)
        tvTimeBorrow = findViewById(R.id.tvTimeBorrow)
        tvBorrowDate = findViewById(R.id.tvBorrowDate)
        tvReturnDate = findViewById(R.id.tvReturnDate)
        tvDeposit = findViewById(R.id.tvDeposit)
        rcvBooks = findViewById(R.id.rcvBooks)

        loadBorrowDetails(maPM)
        loadBookBorrows(maPM)
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
                editBorrowRecord(maPM)
                true
            }

            R.id.menu_delete -> {
                deleteBorrowRecord()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadBorrowDetails(maPM: String) {
        val borrowRecord = borrowRecordDao.getBorrowRecordById(maPM)

        if (borrowRecord != null) {
            tvBorrowRecordID.text = borrowRecord.MaPM
            tvTimeBorrow.text = "${borrowRecord.SoNgayMuon} ngày"
            tvBorrowDate.text = borrowRecord.NgayMuon
            calculateReturnDate(borrowRecord.NgayMuon, borrowRecord.SoNgayMuon)
            tvDeposit.text = String.format("%.0f", borrowRecord.TienCoc)


            val readerDao = ReaderDao(databaseHelper)
            val reader = readerDao.getReaderById(borrowRecord.MaDG)
            val libraryDao = LibrarianDao(databaseHelper)
            val librarian = libraryDao.getLibrarianById(borrowRecord.MaTT)

            reader?.let { tvReaderName.text = reader.HoTen }
            librarian?.let { tvLibrarianName.text = librarian.HoTen }
        } else {
            Toast.makeText(this, "Không tìm thấy phiếu mượn", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadBookBorrows(maPM: String) {
        val bookBorrows = borrowDetailDao.getBorrowDetailById(maPM)

        if (bookBorrows != null) {
            rcvBooks.layoutManager = LinearLayoutManager(this)
            val bookAdapter = BookAdapter(null, bookBorrows, null)
            rcvBooks.adapter = bookAdapter
        } else {
            Toast.makeText(this, "Không tìm thấy chi tiết phiếu mượn", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateReturnDate(dateStr: String, daysToAdd: Int) {
        val borrowDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)

        if (borrowDate != null) {
            val calendar = Calendar.getInstance()
            calendar.time = borrowDate
            calendar.add(Calendar.DAY_OF_MONTH, daysToAdd)

            val returnDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            tvReturnDate.setText(returnDateStr)
        }
    }

    companion object {
        private const val REQUEST_CODE_UPDATE_BORROW = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UPDATE_BORROW && resultCode == RESULT_OK) {
            loadBorrowDetails(maPM)
            loadBookBorrows(maPM)
        }
    }

    private fun editBorrowRecord(maPM: String) {
        val intent = Intent(this, BorrowUpdateActivity::class.java)
        intent.putExtra("BORROW_ID", maPM)
        startActivityForResult(intent, REQUEST_CODE_UPDATE_BORROW)
    }

    private fun deleteBorrowRecord() {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận")
            .setMessage("Bạn có chắc chắn muốn xóa phiếu mượn này không?")
            .setPositiveButton("Có") { _, _ ->
//                val rowsAffected = borrowRecordDao.delete(maPM)
//                if (rowsAffected > 0) {
                    Toast.makeText(this, "Đã xóa phiếu mượn thành công", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this, "Xóa phiếu mượn thất bại", Toast.LENGTH_SHORT).show()
//                }
//                finish()
            }
            .setNegativeButton("Không") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}