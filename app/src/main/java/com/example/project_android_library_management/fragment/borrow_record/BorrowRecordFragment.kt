package com.example.project_android_library_management.fragment.borrow_record

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.BorrowRecordAdapter
import com.example.project_android_library_management.dao.BorrowRecordDao
import com.example.project_android_library_management.model.BorrowRecord
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BorrowRecordFragment : Fragment() {
    private lateinit var rcvBorrowRecord: RecyclerView
    private lateinit var borrowRecordList: ArrayList<BorrowRecord>
    private lateinit var borrowRecordAdapter: BorrowRecordAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var borrowRecordDao: BorrowRecordDao
    private lateinit var edtSearch: SearchView

    private lateinit var btnFilter: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnClose: ImageView
    private lateinit var chkReturned: CheckBox
    private lateinit var chkNotReturned: CheckBox
    private lateinit var chkOverdue: CheckBox
    private lateinit var edtFromDay: EditText
    private lateinit var edtToDay: EditText
    private lateinit var edtReader: EditText
    private lateinit var edtLibrarian: EditText
    private lateinit var fromDateLayout: TextInputLayout
    private lateinit var toDateLayout: TextInputLayout
    private lateinit var btnReset: Button
    private lateinit var btnApply: Button

    companion object {
        private const val REQUEST_CODE_BORROW_LIST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_borrow_record, container, false)

        edtSearch = view.findViewById(R.id.edtSearch)
        rcvBorrowRecord = view.findViewById(R.id.rcvBorrowRecord)
        rcvBorrowRecord.layoutManager = LinearLayoutManager(context)
        rcvBorrowRecord.setHasFixedSize(true)

        btnFilter = view.findViewById(R.id.btnFilter)
        drawerLayout = view.findViewById(R.id.drawer_layout)
        navView = view.findViewById(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        btnClose = headerView.findViewById(R.id.btnClose)
        btnReset = headerView.findViewById(R.id.btnReset)
        btnApply = headerView.findViewById(R.id.btnApply)
        chkReturned = headerView.findViewById(R.id.chkReturned)
        chkNotReturned = headerView.findViewById(R.id.chkNotReturned)
        chkOverdue = headerView.findViewById(R.id.chkOverdue)
        edtFromDay = headerView.findViewById(R.id.edtFromDay)
        edtToDay = headerView.findViewById(R.id.edtToDay)
        edtReader = headerView.findViewById(R.id.edtReader)
        edtLibrarian = headerView.findViewById(R.id.edtLibrarian)
        fromDateLayout = headerView.findViewById(R.id.fromDateLayout)
        toDateLayout = headerView.findViewById(R.id.toDateLayout)

        databaseHelper = DatabaseHelper(requireContext())
        borrowRecordDao = BorrowRecordDao(databaseHelper)
        borrowRecordList = borrowRecordDao.getAllBorrowRecord()

        borrowRecordAdapter = BorrowRecordAdapter(borrowRecordList, object : BorrowRecordAdapter.OnItemClickListener {
            override fun onItemClick(borrowRecord: BorrowRecord) {
                val intent = Intent(context, BorrowDetailActivity::class.java)
                intent.putExtra("BORROW_ID", borrowRecord.MaPM)
                startActivityForResult(intent, REQUEST_CODE_BORROW_LIST)
            }
        })

        rcvBorrowRecord.adapter = borrowRecordAdapter

        val btnAdd = view.findViewById<FloatingActionButton>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val intent = Intent(activity, BorrowAddActivity::class.java)
            startActivity(intent)
        }

        edtSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    performSearch(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    performSearch(it)
                }
                return true
            }
        })

        btnFilter.setOnClickListener {
            drawerLayout.openDrawer(navView)
        }

        btnClose.setOnClickListener {
            drawerLayout.closeDrawer(navView)
            resetFilter()
        }

        btnReset.setOnClickListener {
            resetFilter()
        }

        btnApply.setOnClickListener {
            applyFilter()
        }

        fromDateLayout.setStartIconOnClickListener {
            showDatePickerDialog(edtFromDay)
            fromDateLayout.hint = ""
        }

        toDateLayout.setStartIconOnClickListener {
            showDatePickerDialog(edtToDay)
            toDateLayout.hint = ""
        }

        edtFromDay.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (edtFromDay.text.isEmpty()) {
                    fromDateLayout.hint = ""
                }
            } else {
                if (edtFromDay.text.isEmpty()) {
                    fromDateLayout.hint = "Từ ngày"
                }
            }
        }

        edtToDay.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (edtToDay.text.isEmpty()) {
                    toDateLayout.hint = ""
                }
            } else {
                if (edtToDay.text.isEmpty()) {
                    toDateLayout.hint = "Đến ngày"
                }
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_BORROW_LIST && resultCode == Activity.RESULT_OK) {
            loadBorrowRecordList()
        }
    }

    override fun onResume() {
        super.onResume()
        loadBorrowRecordList()
    }

    private fun loadBorrowRecordList() {
        val borrowRecord = borrowRecordDao.getAllBorrowRecord()
        borrowRecordAdapter.updateData(borrowRecord)
    }

    fun updateBorrowRecordList(newList: List<BorrowRecord>) {
        borrowRecordList.clear()
        borrowRecordList.addAll(newList)
        borrowRecordAdapter.notifyDataSetChanged()
    }

    private fun performSearch(query: String) {
        val listSearch = borrowRecordDao.searchBorrowRecord(query)
        updateBorrowRecordList(listSearch)
    }

    private fun applyFilter() {
        val status = mutableListOf<String>()
        if (chkReturned.isChecked) status.add("Đã trả")
        if (chkNotReturned.isChecked) status.add("Chưa trả")
        if (chkOverdue.isChecked) status.add("Quá hạn")
        val fromDay = edtFromDay.text.toString()
        val toDay = edtToDay.text.toString()
        val reader = edtReader.text.toString()
        val librarian = edtLibrarian.text.toString()

        if (areDatesValid(fromDay, toDay)) {
            val listSearch = borrowRecordDao.searchBorrowByFilter(status, fromDay, toDay, reader, librarian)
            updateBorrowRecordList(listSearch)
            drawerLayout.closeDrawer(navView)
        }
    }

    private fun resetFilter() {
        chkReturned.isChecked = false
        chkNotReturned.isChecked = false
        chkOverdue.isChecked = false
        edtFromDay.text.clear()
        edtToDay.text.clear()
        edtReader.text.clear()
        edtLibrarian.text.clear()
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
            requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val formattedMonth = String.format("%02d", month + 1)
                val formattedDay = String.format("%02d", dayOfMonth)
                val selectedDate = "${year}-${formattedMonth}-${formattedDay}"
                textView.error = null
                textView.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun areDatesValid(fromDay: String, toDay: String): Boolean {
        if (fromDay.isEmpty() && toDay.isEmpty()) {
            return true
        }
        if (fromDay.isEmpty() || toDay.isEmpty()) {
            if (fromDay.isEmpty()) {
                edtFromDay.error = "Ngày (yyyy-MM-dd) không được để trống"
                edtFromDay.requestFocus()
                return false
            }
            if (toDay.isEmpty()) {
                edtToDay.error = "Ngày (yyyy-MM-dd) không được để trống"
                edtToDay.requestFocus()
                return false
            }
        }
        if (!validateDate(fromDay)) {
            edtFromDay.error = "Ngày (yyyy-MM-dd) không hợp lệ"
            edtFromDay.requestFocus()
            return false
        }
        if (!validateDate(toDay)) {
            edtToDay.error = "Ngày (yyyy-MM-dd) không hợp lệ"
            edtToDay.requestFocus()
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