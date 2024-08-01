package com.example.project_android_library_management.fragment.search

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
import com.example.project_android_library_management.model.OrderBook

class SearchBookActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var bookDao: BookDao
    private lateinit var searchBookAdapter: SearchBookAdapter
    private lateinit var bookList: ArrayList<Book>
    private lateinit var bookListCopy: ArrayList<Book>

    private lateinit var edtSearch: SearchView
    private lateinit var rcvList: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val toolbarTitle = intent.getStringExtra("TOOLBAR_TITLE")
        val hideBtnSelect = intent.getBooleanExtra("HIDE_BTN_SELECT", false)

        if (toolbarTitle != null) {
            supportActionBar?.title = toolbarTitle
        }

        edtSearch = findViewById(R.id.edtSearch)

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

        bookListCopy = ArrayList(bookList)

        searchBookAdapter = SearchBookAdapter(this, bookList, hideBtnSelect)

        rcvList.adapter = searchBookAdapter

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

    fun updateBookList(newList: List<Book>) {
        bookList.clear()
        bookList.addAll(newList)
        searchBookAdapter.notifyDataSetChanged()
    }

    private fun performSearch(query: String) {
        val normalizedQuery = query.toLowerCase()
        val listSearch = bookListCopy.filter {
            it.MaSach.toLowerCase().contains(normalizedQuery) ||
            it.ISBN.toLowerCase().contains(normalizedQuery) ||
            it.TenSach.toLowerCase().contains(normalizedQuery) ||
            it.TacGia.toLowerCase().contains(normalizedQuery)
        }
        updateBookList(listSearch)
    }
}