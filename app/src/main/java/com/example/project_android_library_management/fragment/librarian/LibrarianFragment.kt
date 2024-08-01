package com.example.project_android_library_management.fragment.librarian

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.LibrarianAdapter
import com.example.project_android_library_management.dao.LibrarianDao
import com.example.project_android_library_management.model.Librarian
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class LibrarianFragment : Fragment() {
    private lateinit var rcvLibrarians: RecyclerView
    private lateinit var librarianList: ArrayList<Librarian>
    private lateinit var librarianAdapter: LibrarianAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var librarianDao: LibrarianDao
    private lateinit var edtSearch: SearchView

    private lateinit var btnFilter: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnClose: ImageView
    private lateinit var chkMale: CheckBox
    private lateinit var chkFemale: CheckBox
    private lateinit var chkOther: CheckBox
    private lateinit var edtFromDay: EditText
    private lateinit var edtToDay: EditText
    private lateinit var fromDateLayout: TextInputLayout
    private lateinit var toDateLayout: TextInputLayout
    private lateinit var btnReset: Button
    private lateinit var btnApply: Button
    private lateinit var tvDate: TextView

    companion object {
        private const val REQUEST_CODE_LIBRARIAN_LIST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_librarian, container, false)

        edtSearch = view.findViewById(R.id.edtSearch)
        rcvLibrarians = view.findViewById(R.id.rcvLibrarians)
        rcvLibrarians.layoutManager = LinearLayoutManager(context)
        rcvLibrarians.setHasFixedSize(true)

        btnFilter = view.findViewById(R.id.btnFilter)
        drawerLayout = view.findViewById(R.id.drawer_layout)
        navView = view.findViewById(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        btnClose = headerView.findViewById(R.id.btnClose)
        chkMale = headerView.findViewById(R.id.chkMale)
        chkFemale = headerView.findViewById(R.id.chkFemale)
        chkOther = headerView.findViewById(R.id.chkOther)
        edtFromDay = headerView.findViewById(R.id.edtFromDay)
        edtToDay = headerView.findViewById(R.id.edtToDay)
        fromDateLayout = headerView.findViewById(R.id.fromDateLayout)
        toDateLayout = headerView.findViewById(R.id.toDateLayout)
        btnReset = headerView.findViewById(R.id.btnReset)
        btnApply = headerView.findViewById(R.id.btnApply)
        tvDate = headerView.findViewById(R.id.tvDate)
        tvDate.text = "Theo ngày sinh"

        databaseHelper = DatabaseHelper(requireContext())
        librarianDao = LibrarianDao(databaseHelper)
        librarianList = librarianDao.getAllLibrarian()

        librarianAdapter = LibrarianAdapter(librarianList, object : LibrarianAdapter.OnItemClickListener {
            override fun onItemClick(librarian: Librarian) {
                val intent = Intent(context, LibrarianDetailActivity::class.java)
                intent.putExtra("LIBRARIAN_ID", librarian.MaTT)
                startActivityForResult(intent, REQUEST_CODE_LIBRARIAN_LIST)
            }
        })

        rcvLibrarians.adapter = librarianAdapter

        val btnAdd = view.findViewById<FloatingActionButton>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val intent = Intent(activity, LibrarianAddActivity::class.java)
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
            applyFilter()
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
        if (requestCode == REQUEST_CODE_LIBRARIAN_LIST && resultCode == Activity.RESULT_OK) {
            loadReaderList()
        }
    }

    override fun onResume() {
        super.onResume()
        loadReaderList()
    }

    private fun loadReaderList() {
        val librarians = librarianDao.getAllLibrarian()
        librarianAdapter.updateData(librarians)
    }

    fun updateReaderList(newList: List<Librarian>) {
        librarianList.clear()
        librarianList.addAll(newList)
        librarianAdapter.notifyDataSetChanged()
    }

    private fun performSearch(query: String) {
        val listSearch = librarianDao.searchLibrarian(query)
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
            val listSearch = librarianDao.searchLibrarianByFilter(gender, fromDay, toDay)
            updateReaderList(listSearch)
            drawerLayout.closeDrawer(navView)
        }
    }

    private fun resetFilter() {
        chkMale.isChecked = false
        chkFemale.isChecked = false
        chkOther.isChecked = false
        fromDateLayout.hint = "Từ ngày"
        toDateLayout.hint = "Đến ngày"
        edtFromDay.text.clear()
        edtToDay.text.clear()
    }

    private fun showDatePickerDialog(textView: TextView) {
        val calendar: Calendar = Calendar.getInstance()
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