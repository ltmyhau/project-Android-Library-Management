package com.example.project_android_library_management.fragment.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.SearchBookAdapter
import com.example.project_android_library_management.adapter.SearchBorrowAdapter
import com.example.project_android_library_management.dao.BorrowRecordDao
import com.example.project_android_library_management.dao.ReturnRecordDao
import com.example.project_android_library_management.model.Book
import com.example.project_android_library_management.model.BorrowRecord

class SearchBorrowActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var borrowRecordDao: BorrowRecordDao
    private lateinit var returnRecordDao: ReturnRecordDao
    private lateinit var searchBorrowAdapter: SearchBorrowAdapter
    private lateinit var borrowRecordList: ArrayList<BorrowRecord>
    private lateinit var borrowRecordListCopy: ArrayList<BorrowRecord>

    private lateinit var edtSearch: SearchView
    private lateinit var rcvList: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        edtSearch = findViewById(R.id.edtSearch)

        rcvList = findViewById(R.id.rcvList)
        rcvList.layoutManager = LinearLayoutManager(this)

        databaseHelper = DatabaseHelper(this)
        borrowRecordDao = BorrowRecordDao(databaseHelper)
        returnRecordDao = ReturnRecordDao(databaseHelper)

        val source = intent.getStringExtra("SOURCE")
        borrowRecordList = if (source == "BorrowRecordNotReturned") {
            val borrowList = borrowRecordDao.getAllBorrowRecord()
            val returnList = returnRecordDao.getAllReturnRecord()
            val returnedMaPMs = returnList.map { it.MaPM }.toSet()
            ArrayList(borrowList.filter { it.MaPM !in returnedMaPMs })
        } else {
            borrowRecordDao.getAllBorrowRecord()
        }

        borrowRecordListCopy = ArrayList(borrowRecordList)

        searchBorrowAdapter = SearchBorrowAdapter(this, borrowRecordList)
        rcvList.adapter = searchBorrowAdapter

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
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun updateBorrowList(newList: List<BorrowRecord>) {
        borrowRecordList.clear()
        borrowRecordList.addAll(newList)
        searchBorrowAdapter.notifyDataSetChanged()
    }

    private fun performSearch(query: String) {
        val normalizedQuery = query.toLowerCase()
        val listSearch = borrowRecordListCopy.filter {
            it.MaPM.toLowerCase().contains(normalizedQuery) ||
            it.SoNgayMuon.toString().contains(normalizedQuery) ||
            it.TienCoc.toString().contains(normalizedQuery) ||
            it.MaDG.toLowerCase().contains(normalizedQuery) ||
            it.MaTT.toLowerCase().contains(normalizedQuery)
        }
        updateBorrowList(listSearch)
    }
}