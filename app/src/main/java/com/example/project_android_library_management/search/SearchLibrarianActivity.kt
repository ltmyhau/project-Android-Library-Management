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
        edtSearch.requestFocus()

        rcvList = findViewById(R.id.rcvList)
        rcvList.layoutManager = LinearLayoutManager(this)

        databaseHelper = DatabaseHelper(this)
        librarianDao = LibrarianDao(databaseHelper)
        librarianList = librarianDao.getAllLibrarian()

        searchLibrarianAdapter = SearchLibrarianAdapter(this, librarianList)
        rcvList.adapter = searchLibrarianAdapter

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}