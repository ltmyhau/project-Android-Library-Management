package com.example.project_android_library_management.search

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
import com.example.project_android_library_management.model.Book
import com.example.project_android_library_management.model.BorrowRecord

class SearchBorrowActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var borrowRecordDao: BorrowRecordDao
    private lateinit var searchBorrowAdapter: SearchBorrowAdapter
    private lateinit var borrowRecordList: ArrayList<BorrowRecord>

    private lateinit var edtSearch: SearchView
    private lateinit var rcvList: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        edtSearch = findViewById(R.id.edtSearch)
        edtSearch.requestFocus()

        rcvList = findViewById(R.id.rcvList)
        rcvList.layoutManager = LinearLayoutManager(this)

        databaseHelper = DatabaseHelper(this)
        borrowRecordDao = BorrowRecordDao(databaseHelper)

        val source = intent.getStringExtra("SOURCE")
        borrowRecordList = if (source == "BorrowRecordNotReturned") {
            intent.getSerializableExtra("BORROW_LIST") as ArrayList<BorrowRecord>
        } else {
            borrowRecordDao.getAllBorrowRecord()
        }

        searchBorrowAdapter = SearchBorrowAdapter(this, borrowRecordList)
        rcvList.adapter = searchBorrowAdapter

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}