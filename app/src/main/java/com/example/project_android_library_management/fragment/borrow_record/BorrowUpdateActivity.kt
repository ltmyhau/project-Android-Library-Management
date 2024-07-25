package com.example.project_android_library_management.fragment.borrow_record

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.SearchReaderActivity
import com.example.project_android_library_management.adapter.BookBorrowAdapter
import com.example.project_android_library_management.dao.BorrowDetailDao
import com.example.project_android_library_management.dao.BorrowRecordDao
import com.example.project_android_library_management.dao.LibrarianDao
import com.example.project_android_library_management.dao.ReaderDao
import com.example.project_android_library_management.model.BorrowDetail
import com.example.project_android_library_management.model.BorrowRecord
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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
    private lateinit var bookBorrows: ArrayList<BorrowDetail>

    private var maPM: String = ""
    private var readerId: String = ""
    private var librarianId: String = ""
    private var timeBorrow: Int? = null

    private lateinit var edtBorrowId: TextInputEditText
    private lateinit var spnLibrarian: AutoCompleteTextView
    private lateinit var edtReaderName: TextInputEditText
    private lateinit var spnTimeBorrow: AutoCompleteTextView
    private lateinit var edtBorrowDate: TextInputEditText
    private lateinit var edtReturnDate: TextInputEditText
    private lateinit var edtDeposit: TextInputEditText
    private lateinit var edtNotes: TextInputEditText
    private lateinit var rcvBooks: RecyclerView

    companion object {
        private const val REQUEST_CODE_READER_ID = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_borrow_edit)

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
        edtNotes = findViewById(R.id.edtNotes)
        rcvBooks = findViewById(R.id.rcvBooks)

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        edtBorrowDate.setText(sdf.format(Date()))

        loadTimeBorrowSpinner()
        loadLibrarianSpinner()

        loadBorrowDetails(maPM)
        loadBookBorrows(maPM)

        val btnSave = findViewById<AppCompatButton>(R.id.btnSave)
        btnSave.setOnClickListener {
            saveBorrowRecord()
        }

        val borrowDateLayout = findViewById<TextInputLayout>(R.id.borrowDateLayout)
        borrowDateLayout.setStartIconOnClickListener {
            showDatePickerDialog(edtBorrowDate)
        }

        edtReaderName.setOnClickListener {
            val intent = Intent(this, SearchReaderActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_READER_ID)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_READER_ID && resultCode == RESULT_OK) {
            readerId = data?.getStringExtra("READER_ID") ?: ""
            if (readerId != null && readerId.isNotEmpty()) {
                val reader = readerDao.getReaderById(readerId)
                edtReaderName.setText(reader?.HoTen ?: "")
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
                updateReturnDate()
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
                librarianId = it.MaTT
            }
            spnLibrarian.setTextColor(ContextCompat.getColor(this, R.color.text_dark_color))
        }
    }

    private fun loadTimeBorrowSpinner() {
        val timeBorrowArray = resources.getStringArray(R.array.book_lend_period)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, timeBorrowArray)
        spnTimeBorrow.setAdapter(adapter)

        spnTimeBorrow.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = timeBorrowArray[position]
            timeBorrow = extractNumberFromString(selectedItem)
            updateReturnDate()
        }
    }

    private fun extractNumberFromString(text: String): Int? {
        return text.replace(Regex("[^0-9]"), "").toIntOrNull()
    }

    private fun loadBorrowDetails(maPM: String) {
        val borrowRecord = borrowRecordDao.getBorrowRecordById(maPM)

        if (borrowRecord != null) {
            edtBorrowId.setText(borrowRecord.MaPM)
            timeBorrow = borrowRecord.SoNgayMuon
            setSpinnerValue(spnTimeBorrow, "${timeBorrow} ngày")
            edtBorrowDate.setText(borrowRecord.NgayMuon)
            edtReturnDate.setText(borrowRecord.NgayMuon)
            edtDeposit.setText(String.format("%.0f", borrowRecord.TienCoc))
            edtNotes.setText(borrowRecord.GhiChu)

            updateReturnDate()

            readerId = borrowRecord.MaDG
            val readerDao = ReaderDao(databaseHelper)
            val reader = readerDao.getReaderById(readerId)

            librarianId = borrowRecord.MaTT
            val libraryDao = LibrarianDao(databaseHelper)
            val librarian = libraryDao.getLibrarianById(librarianId)

            reader?.let { edtReaderName.setText(it.HoTen) }
            librarian?.let { setSpinnerValue(spnLibrarian, it.HoTen) }
        } else {
            Toast.makeText(this, "Không tìm thấy phiếu mượn", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadBookBorrows(maPM: String) {
        bookBorrows = borrowDetailDao.getBorrowDetailById(maPM)

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

    private fun updateReturnDate() {
        val borrowDateStr = edtBorrowDate.text.toString()
        val borrowDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(borrowDateStr)
        val daysToAdd = timeBorrow ?: 0

        if (borrowDate != null) {
            val calendar = Calendar.getInstance()
            calendar.time = borrowDate
            calendar.add(Calendar.DAY_OF_MONTH, daysToAdd)

            val returnDateStr =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            edtReturnDate.setText(returnDateStr)
        }
    }

    private fun saveBorrowRecord() {
        val borrowDate = edtBorrowDate.text.toString()
        val deposit = edtDeposit.text.toString().toDoubleOrNull() ?: 0.0
        val notes = edtNotes.text.toString()

        if (validateFields()) {
            val borrowRecord = BorrowRecord(maPM, borrowDate, timeBorrow!!, deposit, notes, readerId, librarianId)
            val rowsAffected = borrowRecordDao.update(borrowRecord)
            if (rowsAffected > 0 && saveBookBorrow()) {
                Toast.makeText(this, "Cập nhật thông tin phiếu nhập thành công", Toast.LENGTH_SHORT)
                    .show()
                val resultIntent = Intent()
                resultIntent.putExtra("BORROW_ID", maPM)
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Cập nhật thông tin phiếu nhập thất bại", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun saveBookBorrow(): Boolean {
        var allSuccess = true

        try {
            val rowsDeleted = borrowDetailDao.delete(maPM)
            if (rowsDeleted <= 0) {
                allSuccess = false
            }
        } catch (e: Exception) {
            Log.e("RefreshBookBorrow", "Lỗi xóa chi tiết phiếu mượn", e)
            allSuccess = false
        }

        if (allSuccess) {
            for (borrowDetail in bookBorrows) {
                val rowsAffected = borrowDetailDao.insert(borrowDetail)
                if (rowsAffected <= 0) {
                    allSuccess = false
                    break
                }
            }
        }
        return allSuccess
    }

    private fun validateFields(): Boolean {
        if (spnLibrarian.text.isNullOrEmpty() || spnLibrarian.text.toString() == "Thủ thư") {
            Toast.makeText(this, "Vui lòng chọn thủ thư", Toast.LENGTH_SHORT).show()
            return false
        }
        if (edtReaderName.text.isNullOrEmpty() || edtReaderName.text.toString() == "Độc giả") {
            edtReaderName.error = "Vui lòng chọn độc giả"
            edtReaderName.requestFocus()
            return false
        }
        if (spnTimeBorrow.text.isNullOrEmpty() || spnTimeBorrow.text.toString() == "Thời gian mượn") {
            Toast.makeText(this, "Vui lòng chọn thủ thư", Toast.LENGTH_SHORT).show()
            return false
        }
        if (edtBorrowDate.text.isNullOrEmpty()) {
            edtBorrowDate.error = "Ngày mượn (yyyy-MM-dd) không được để trống"
            edtBorrowDate.requestFocus()
            return false
        } else if (!validateDate(edtBorrowDate.text.toString())) {
            edtBorrowDate.error = "Ngày mượn (yyyy-MM-dd) không hợp lệ"
            edtBorrowDate.requestFocus()
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