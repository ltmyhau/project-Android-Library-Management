package com.example.project_android_library_management.fragment.book

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.BookAdapter
import com.example.project_android_library_management.dao.BookDao
import com.example.project_android_library_management.model.Book

class BookListFragment : Fragment() {
    private lateinit var rcvBook: RecyclerView
    private lateinit var bookList: ArrayList<Book>
    private lateinit var bookAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_list, container, false)

        rcvBook = view.findViewById(R.id.rcvBook)
        rcvBook.layoutManager = LinearLayoutManager(context)
        rcvBook.setHasFixedSize(true)

        val databaseHelper = DatabaseHelper(requireContext())
        val bookDao = BookDao(databaseHelper)
        bookList = bookDao.getAllBooks()
        bookAdapter = BookAdapter(bookList)
        rcvBook.adapter = bookAdapter

        return view
    }
}