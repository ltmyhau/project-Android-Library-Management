package com.example.project_android_library_management.search

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.SearchReaderAdapter
import com.example.project_android_library_management.dao.ReaderDao
import com.example.project_android_library_management.model.Reader

class SearchReaderActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var readerDao: ReaderDao
    private lateinit var searchReaderAdapter: SearchReaderAdapter
    private lateinit var readerList: ArrayList<Reader>

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
        readerDao = ReaderDao(databaseHelper)
        readerList = readerDao.getAllReaders()

        searchReaderAdapter = SearchReaderAdapter(this, readerList)
        rcvList.adapter = searchReaderAdapter

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}