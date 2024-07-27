package com.example.project_android_library_management

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.adapter.SearchPublisherAdapter
import com.example.project_android_library_management.dao.PublisherDao
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
        edtSearch.requestFocus()

        rcvList = findViewById(R.id.rcvList)
        rcvList.layoutManager = LinearLayoutManager(this)

        databaseHelper = DatabaseHelper(this)
        publisherDao = PublisherDao(databaseHelper)
        publisherList = publisherDao.getAllPublisher()

        searchPublisherAdapter = SearchPublisherAdapter(this, publisherList)
        rcvList.adapter = searchPublisherAdapter

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}