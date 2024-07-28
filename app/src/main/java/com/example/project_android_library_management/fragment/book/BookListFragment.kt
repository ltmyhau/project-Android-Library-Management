package com.example.project_android_library_management.fragment.book

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.BookAdapter
import com.example.project_android_library_management.dao.BookDao
import com.example.project_android_library_management.model.Book
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BookListFragment : Fragment() {
    private lateinit var rcvBook: RecyclerView
    private lateinit var bookList: ArrayList<Book>
    private lateinit var bookAdapter: BookAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var bookDao: BookDao

    companion object {
        private const val REQUEST_CODE_BOOK_DETAIL = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_list, container, false)

        rcvBook = view.findViewById(R.id.rcvBook)
        rcvBook.layoutManager = LinearLayoutManager(context)
        rcvBook.setHasFixedSize(true)

        databaseHelper = DatabaseHelper(requireContext())
        bookDao = BookDao(databaseHelper)
        bookList = bookDao.getAllBooks()

        bookAdapter = BookAdapter(bookList, null, null, null, object : BookAdapter.OnItemClickListener {
            override fun onItemClick(book: Book) {
                val intent = Intent(context, BookDetailActivity::class.java)
                intent.putExtra("BOOK_ID", book.MaSach)
                startActivityForResult(intent, REQUEST_CODE_BOOK_DETAIL)
            }
        })

        rcvBook.adapter = bookAdapter

        val btnAdd = view.findViewById<FloatingActionButton>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val intent = Intent(activity, BookAddActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_BOOK_DETAIL && resultCode == Activity.RESULT_OK) {
            loadBookList()
        }
    }

    override fun onResume() {
        super.onResume()
        loadBookList()
    }

    private fun loadBookList() {
        val books = bookDao.getAllBooks()
        bookAdapter.updateData(books)
    }

    fun updateBookList(newBookList: List<Book>) {
        bookList.clear()
        bookList.addAll(newBookList)
        bookAdapter.notifyDataSetChanged()
    }
}