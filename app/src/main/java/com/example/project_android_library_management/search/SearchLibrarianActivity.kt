package com.example.project_android_library_management.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.SearchLibrarianAdapter
import com.example.project_android_library_management.dao.LibrarianDao
import com.example.project_android_library_management.model.Librarian
import com.example.project_android_library_management.model.Reader

class SearchLibrarianActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var librarianDao: LibrarianDao
    private lateinit var searchLibrarianAdapter: SearchLibrarianAdapter
    private lateinit var librarianList: ArrayList<Librarian>

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
        librarianDao = LibrarianDao(databaseHelper)
        librarianList = librarianDao.getAllLibrarian()

        searchLibrarianAdapter = SearchLibrarianAdapter(this, librarianList)
        rcvList.adapter = searchLibrarianAdapter

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

    fun updateReaderList(newList: List<Librarian>) {
        librarianList.clear()
        librarianList.addAll(newList)
        searchLibrarianAdapter.notifyDataSetChanged()
    }

    private fun performSearch(query: String) {
        val listSearch = librarianDao.searchLibrarian(query)
        updateReaderList(listSearch)
    }
}