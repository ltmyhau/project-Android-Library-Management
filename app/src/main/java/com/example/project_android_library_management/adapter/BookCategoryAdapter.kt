package com.example.project_android_library_management.adapter

import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.dao.BookDao
import com.example.project_android_library_management.fragment.book.BookDetailActivity
import com.example.project_android_library_management.model.BookCategory
import com.example.project_android_library_management.fragment.search.SearchBookActivity

class BookCategoryAdapter(private val categoryList: ArrayList<BookCategory>) : RecyclerView.Adapter<BookCategoryAdapter.CategoryViewHolder>() {
    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvShowAll: TextView = itemView.findViewById(R.id.tvShowAll)
        val rcvBook: RecyclerView = itemView.findViewById(R.id.rcvCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.tvCategory.text = category.TenLoai

        val bookDao = BookDao(DatabaseHelper(holder.itemView.context))
        val bookList = bookDao.getBooksByCategoryId(category.MaLoai)

        holder.rcvBook.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)

        holder.rcvBook.adapter = BookByCategoryAdapter(bookList) { selectedBook ->
            val intent = Intent(holder.itemView.context, BookDetailActivity::class.java)
            intent.putExtra("BOOK_ID", selectedBook.MaSach)
            holder.itemView.context.startActivity(intent)
        }

        holder.tvShowAll.paintFlags = holder.tvShowAll.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.tvShowAll.setOnClickListener {
            val intent = Intent(holder.itemView.context, SearchBookActivity::class.java)
            intent.putExtra("SOURCE", "BookCategoryList")
            intent.putExtra("CATEGORY_ID", category.MaLoai)
            intent.putExtra("TOOLBAR_TITLE", "${category.TenLoai}")
            intent.putExtra("HIDE_BTN_SELECT", true)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}