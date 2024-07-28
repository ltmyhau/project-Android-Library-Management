package com.example.project_android_library_management.fragment.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.BookCategoryAdapter
import com.example.project_android_library_management.dao.BookCategoryDao
import com.example.project_android_library_management.model.Book
import com.example.project_android_library_management.model.BookCategory

class BookCategoryFragment : Fragment() {
    private lateinit var rcvCategory: RecyclerView
    private lateinit var bookCategoryList: ArrayList<BookCategory>
    private lateinit var bookCategoryAdapter: BookCategoryAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var bookCategoryDao: BookCategoryDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_category, container, false)

        rcvCategory = view.findViewById(R.id.rcvCategory)
        rcvCategory.layoutManager = LinearLayoutManager(context)

        databaseHelper = DatabaseHelper(requireContext())
        bookCategoryDao = BookCategoryDao(databaseHelper)
        bookCategoryList = bookCategoryDao.getAllBookCategories()

        bookCategoryAdapter = BookCategoryAdapter(bookCategoryList)
        rcvCategory.adapter = bookCategoryAdapter

        return view
    }

    fun updateCategoryList(newList: List<BookCategory>) {
        bookCategoryList.clear()
        bookCategoryList.addAll(newList)
        bookCategoryAdapter.notifyDataSetChanged()
    }
}