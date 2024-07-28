package com.example.project_android_library_management.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.SearchBookAdapter
import com.example.project_android_library_management.dao.BookDao
import com.example.project_android_library_management.dao.BorrowDetailDao
import com.example.project_android_library_management.model.Book

class SearchBookActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var bookDao: BookDao
    private lateinit var searchBookAdapter: SearchBookAdapter
    private lateinit var bookList: ArrayList<Book>

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
        bookDao = BookDao(databaseHelper)

        val source = intent.getStringExtra("SOURCE")
        bookList = if (source == "BookList") {
            intent.getSerializableExtra("BOOK_LIST") as ArrayList<Book>
        } else {
            bookDao.getAllBooks()
        }

        searchBookAdapter = SearchBookAdapter(this, bookList)

        rcvList.adapter = searchBookAdapter
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}