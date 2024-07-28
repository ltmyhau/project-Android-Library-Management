package com.example.project_android_library_management.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.SearchPublisherAdapter
import com.example.project_android_library_management.dao.PublisherDao
import com.example.project_android_library_management.model.Librarian
import com.example.project_android_library_management.model.Publisher

class SearchPublisherActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var publisherDao: PublisherDao
    private lateinit var searchPublisherAdapter: SearchPublisherAdapter
    private lateinit var publisherList: ArrayList<Publisher>

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
        publisherDao = PublisherDao(databaseHelper)
        publisherList = publisherDao.getAllPublisher()

        searchPublisherAdapter = SearchPublisherAdapter(this, publisherList)
        rcvList.adapter = searchPublisherAdapter

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

    fun updatePublisherList(newList: List<Publisher>) {
        publisherList.clear()
        publisherList.addAll(newList)
        searchPublisherAdapter.notifyDataSetChanged()
    }

    private fun performSearch(query: String) {
        val listSearch = publisherDao.searchPublisher(query)
        updatePublisherList(listSearch)
    }
}