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

        rcvList = findViewById(R.id.rcvList)
        rcvList.layoutManager = LinearLayoutManager(this)

        databaseHelper = DatabaseHelper(this)
        readerDao = ReaderDao(databaseHelper)
        readerList = readerDao.getAllReaders()

        searchReaderAdapter = SearchReaderAdapter(this, readerList)
        rcvList.adapter = searchReaderAdapter

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

    fun updateReaderList(newList: List<Reader>) {
        readerList.clear()
        readerList.addAll(newList)
        searchReaderAdapter.notifyDataSetChanged()
    }

    private fun performSearch(query: String) {
        val listSearch = readerDao.searchReader(query)
        updateReaderList(listSearch)
    }
}