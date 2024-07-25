package com.example.project_android_library_management.fragment.borrow_record

import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BorrowUpdateActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var readerDao: ReaderDao
    private lateinit var librarianDao: LibrarianDao
    private lateinit var borrowRecordDao: BorrowRecordDao
    private lateinit var borrowDetailDao: BorrowDetailDao

    private var maPM: String = ""
    private var readerId: String = ""

    private lateinit var edtBorrowId: TextInputEditText
    private lateinit var spnLibrarian: AutoCompleteTextView
    private lateinit var edtReaderName: TextInputEditText
    private lateinit var spnTimeBorrow: AutoCompleteTextView
    private lateinit var edtBorrowDate: TextInputEditText
    private lateinit var edtReturnDate: TextInputEditText
    private lateinit var edtDeposit: TextInputEditText
    private lateinit var rcvBooks: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_borrow_update)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        maPM = intent.getStringExtra("BORROW_ID") ?: ""

        databaseHelper = DatabaseHelper(this)
        readerDao = ReaderDao(databaseHelper)
        librarianDao = LibrarianDao(databaseHelper)
        borrowRecordDao = BorrowRecordDao(databaseHelper)
        borrowDetailDao = BorrowDetailDao(databaseHelper)

        edtBorrowId = findViewById(R.id.edtBorrowId)
        edtReaderName = findViewById(R.id.edtReaderName)
        spnLibrarian = findViewById(R.id.spnLibrarian)
        spnTimeBorrow = findViewById(R.id.spnTimeBorrow)
        edtBorrowDate = findViewById(R.id.edtBorrowDate)
        edtReturnDate = findViewById(R.id.edtReturnDate)
        edtDeposit = findViewById(R.id.edtDeposit)
        rcvBooks = findViewById(R.id.rcvBooks)

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        edtBorrowDate.setText(sdf.format(Date()))

        loadTimeBorrowSpinner()
        loadLibrarianSpinner()

        loadBorrowDetails(maPM)
        loadBookBorrows(maPM)

        val btnSave = findViewById<AppCompatButton>(R.id.btnSave)
        btnSave.setOnClickListener {
            saveReaderDetails()
        }

        val borrowDateLayout = findViewById<TextInputLayout>(R.id.borrowDateLayout)
        borrowDateLayout.setStartIconOnClickListener {
            showDatePickerDialog(edtBorrowDate)
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
                val selectedDate = "${year}-${month + 1}-${dayOfMonth}"
                textView.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun loadLibrarianSpinner() {
        val libraries = librarianDao.getAllLibrarian()
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, libraries.map { it.HoTen })
        adapter.setDropDownViewResource(R.layout.dropdown_item)
        spnLibrarian.setAdapter(adapter)

        spnLibrarian.setOnItemClickListener { parent, _, position, _ ->
            val selectedCategoryName = parent.getItemAtPosition(position) as String
            val selectedCategory = libraries.find { it.HoTen == selectedCategoryName }
            selectedCategory?.let {
                readerId = it.MaTT
            }
            spnLibrarian.setTextColor(ContextCompat.getColor(this, R.color.text_dark_color))
        }
    }

    private fun loadTimeBorrowSpinner() {
        val timeBorrow = resources.getStringArray(R.array.book_lend_period)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, timeBorrow)
        spnTimeBorrow.setAdapter(adapter)
    }

    private fun loadBorrowDetails(maPM: String) {
        val borrowRecord = borrowRecordDao.getBorrowRecordById(maPM)

        if (borrowRecord != null) {
            edtBorrowId.setText(borrowRecord.MaPM)
            setSpinnerValue(spnTimeBorrow, borrowRecord.SoNgayMuon.toString() + " ngày")
            edtBorrowDate.setText(borrowRecord.NgayMuon)
            edtReturnDate.setText(borrowRecord.NgayMuon)
            edtDeposit.setText(borrowRecord.TienCoc.toString())

            val readerDao = ReaderDao(databaseHelper)
            val reader = readerDao.getReaderById(borrowRecord.MaDG)
            val libraryDao = LibrarianDao(databaseHelper)
            val librarian = libraryDao.getLibrarianById(borrowRecord.MaTT)

            reader?.let { edtReaderName.setText(it.HoTen) }
            librarian?.let { setSpinnerValue(spnLibrarian, it.HoTen) }
        } else {
            Toast.makeText(this, "Không tìm thấy phiếu mượn", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadBookBorrows(maPM: String) {
        val bookBorrows = borrowDetailDao.getBorrowDetailById(maPM)

        if (bookBorrows != null) {
            rcvBooks.layoutManager = LinearLayoutManager(this)
            val bookBorrowAdapter = BookBorrowAdapter(bookBorrows)
            rcvBooks.adapter = bookBorrowAdapter
        } else {
            Toast.makeText(this, "Không tìm thấy chi tiết phiếu mượn", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setSpinnerValue(spinner: AutoCompleteTextView, value: String) {
        val adapter = spinner.adapter
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i).toString() == value) {
                spinner.setText(value, false)
                return
            }
        }
    }

    private fun saveReaderDetails() {

    }

}