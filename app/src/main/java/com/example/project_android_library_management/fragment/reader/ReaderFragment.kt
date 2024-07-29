package com.example.project_android_library_management.fragment.reader

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.ReaderAdapter
import com.example.project_android_library_management.dao.BookDao
import com.example.project_android_library_management.dao.ReaderDao
import com.example.project_android_library_management.model.Reader
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReaderFragment : Fragment() {
    private lateinit var rcvReaders: RecyclerView
    private lateinit var readerList: ArrayList<Reader>
    private lateinit var readerAdapter: ReaderAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var readerDao: ReaderDao
    private lateinit var edtSearch: SearchView

    private lateinit var btnFilter: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var chkMale: CheckBox
    private lateinit var chkFemale: CheckBox
    private lateinit var chkOther: CheckBox
    private lateinit var edtFromDay: EditText
    private lateinit var edtToDay: EditText
    private lateinit var fromDateLayout: TextInputLayout
    private lateinit var toDateLayout: TextInputLayout
    private lateinit var btnReset: Button
    private lateinit var btnApply: Button

    companion object {
        private const val REQUEST_CODE_READER_LIST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reader, container, false)

        edtSearch = view.findViewById(R.id.edtSearch)
        rcvReaders = view.findViewById(R.id.rcvReaders)
        rcvReaders.layoutManager = LinearLayoutManager(context)
        rcvReaders.setHasFixedSize(true)

        btnFilter = view.findViewById(R.id.btnFilter)
        drawerLayout = view.findViewById(R.id.drawer_layout)
        navView = view.findViewById(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        chkMale = headerView.findViewById(R.id.chkMale)
        chkFemale = headerView.findViewById(R.id.chkFemale)
        chkOther = headerView.findViewById(R.id.chkOther)
        edtFromDay = headerView.findViewById(R.id.edtFromDay)
        edtToDay = headerView.findViewById(R.id.edtToDay)
        fromDateLayout = headerView.findViewById(R.id.fromDateLayout)
        toDateLayout = headerView.findViewById(R.id.toDateLayout)
        btnReset = headerView.findViewById(R.id.btnReset)
        btnApply = headerView.findViewById(R.id.btnApply)

        databaseHelper = DatabaseHelper(requireContext())
        readerDao = ReaderDao(databaseHelper)
        readerList = readerDao.getAllReaders()

        readerAdapter = ReaderAdapter(readerList, object : ReaderAdapter.OnItemClickListener {
            override fun onItemClick(reader: Reader) {
                val intent = Intent(context, ReaderDetailActivity::class.java)
                intent.putExtra("READER_ID", reader.MaDG)
                startActivityForResult(intent, REQUEST_CODE_READER_LIST)
            }
        })

        rcvReaders.adapter = readerAdapter

        val btnAdd = view.findViewById<FloatingActionButton>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val intent = Intent(activity, ReaderAddActivity::class.java)
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
        if (requestCode == REQUEST_CODE_READER_LIST && resultCode == Activity.RESULT_OK) {
            loadReaderList()
        }
    }

    override fun onResume() {
        super.onResume()
        loadReaderList()
    }

    private fun loadReaderList() {
        val readers = readerDao.getAllReaders()
        readerAdapter.updateData(readers)
    }

    fun updateReaderList(newList: List<Reader>) {
        readerList.clear()
        readerList.addAll(newList)
        readerAdapter.notifyDataSetChanged()
    }

    private fun performSearch(query: String) {
        val listSearch = readerDao.searchReader(query)
        updateReaderList(listSearch)
    }

    private fun applyFilter() {
        val gender = mutableListOf<String>()
        if (chkMale.isChecked) gender.add("Nam")
        if (chkFemale.isChecked) gender.add("Nữ")
        if (chkOther.isChecked) gender.add("Khác")
        val fromDay = edtFromDay.text.toString()
        val toDay = edtToDay.text.toString()
        if (areDatesValid(fromDay, toDay)) {
            val listSearch = readerDao.searchReaderByFilter(gender, fromDay, toDay)
            updateReaderList(listSearch)
            drawerLayout.closeDrawer(navView)
        }
    }

    private fun resetFilter() {
        chkMale.isChecked = false
        chkFemale.isChecked = false
        chkOther.isChecked = false
        edtFromDay.text.clear()
        edtToDay.text.clear()
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