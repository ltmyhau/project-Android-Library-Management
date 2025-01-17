package com.example.project_android_library_management.fragment.order_book

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.OrderBookAdapter
import com.example.project_android_library_management.dao.OrderBookDao
import com.example.project_android_library_management.dao.PublisherDao
import com.example.project_android_library_management.model.OrderBook
import com.example.project_android_library_management.model.ReturnRecord
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OrderBookFragment : Fragment() {
    private lateinit var rcvOrderBoooks: RecyclerView
    private lateinit var orderBookList: ArrayList<OrderBook>
    private lateinit var orderBookAdapter: OrderBookAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var orderBookDao: OrderBookDao
    private lateinit var edtSearch: SearchView

    private lateinit var btnFilter: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnClose: ImageView
    private lateinit var edtFromDay: EditText
    private lateinit var edtToDay: EditText
    private lateinit var spnPublisher: AutoCompleteTextView
    private lateinit var edtLibrarian: EditText
    private lateinit var fromDateLayout: TextInputLayout
    private lateinit var toDateLayout: TextInputLayout
    private lateinit var btnReset: Button
    private lateinit var btnApply: Button

    private var publisherId: String = ""

    companion object {
        private const val REQUEST_CODE_ORDER_LIST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_book, container, false)

        edtSearch = view.findViewById(R.id.edtSearch)
        rcvOrderBoooks = view.findViewById(R.id.rcvOrderBoooks)
        rcvOrderBoooks.layoutManager = LinearLayoutManager(context)
        rcvOrderBoooks.setHasFixedSize(true)

        btnFilter = view.findViewById(R.id.btnFilter)
        drawerLayout = view.findViewById(R.id.drawer_layout)
        navView = view.findViewById(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        btnClose = headerView.findViewById(R.id.btnClose)
        btnReset = headerView.findViewById(R.id.btnReset)
        btnApply = headerView.findViewById(R.id.btnApply)
        edtFromDay = headerView.findViewById(R.id.edtFromDay)
        edtToDay = headerView.findViewById(R.id.edtToDay)
        spnPublisher = headerView.findViewById(R.id.spnPublisher)
        edtLibrarian = headerView.findViewById(R.id.edtLibrarian)
        fromDateLayout = headerView.findViewById(R.id.fromDateLayout)
        toDateLayout = headerView.findViewById(R.id.toDateLayout)

        databaseHelper = DatabaseHelper(requireContext())
        orderBookDao = OrderBookDao(databaseHelper)
        orderBookList = orderBookDao.getAllOrderBook()

        orderBookAdapter = OrderBookAdapter(orderBookList, object : OrderBookAdapter.OnItemClickListener {
            override fun onItemClick(orderBook: OrderBook) {
                val intent = Intent(context, OrderDetailActivity::class.java)
                intent.putExtra("ORDER_ID", orderBook.MaPD)
                startActivityForResult(intent, REQUEST_CODE_ORDER_LIST)
            }
        })

        rcvOrderBoooks.adapter = orderBookAdapter

        val btnAdd = view.findViewById<FloatingActionButton>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val intent = Intent(activity, OrderAddActivity::class.java)
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
            loadPublisherSpinner()
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
        if (requestCode == REQUEST_CODE_ORDER_LIST && resultCode == Activity.RESULT_OK) {
            loadOrderBookList()
        }
    }

    override fun onResume() {
        super.onResume()
        loadOrderBookList()
    }

    private fun loadOrderBookList() {
        val orderBook = orderBookDao.getAllOrderBook()
        orderBookAdapter.updateData(orderBook)
    }

    fun updateOrderBookList(newList: List<OrderBook>) {
        orderBookList.clear()
        orderBookList.addAll(newList)
        orderBookAdapter.notifyDataSetChanged()
    }

    private fun performSearch(query: String) {
        val listSearch = orderBookDao.searchOrderBook(query)
        updateOrderBookList(listSearch)
    }

    private fun loadPublisherSpinner() {
        val publisherDao = PublisherDao(DatabaseHelper(requireContext()))
        val publishers = publisherDao.getAllPublisher()
        val publisherArray = publishers.map { it.TenNXB }.toTypedArray()
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, publisherArray)
        spnPublisher.setAdapter(adapter)

        spnPublisher.setOnItemClickListener { parent, _, position, _ ->
            publisherId = publishers[position].MaNXB
        }
    }

    private fun applyFilter() {
        val fromDay = edtFromDay.text.toString()
        val toDay = edtToDay.text.toString()
        val librarian = edtLibrarian.text.toString()

        if (areDatesValid(fromDay, toDay)) {
            val listSearch = orderBookDao.searchOrderByFilter(fromDay, toDay, publisherId, librarian)
            updateOrderBookList(listSearch)
            drawerLayout.closeDrawer(navView)
        }
    }

    private fun resetFilter() {
        fromDateLayout.hint = "Từ ngày"
        toDateLayout.hint = "Đến ngày"
        edtFromDay.text.clear()
        edtToDay.text.clear()
        publisherId = ""
        spnPublisher.setText("Nhà xuất bản")
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