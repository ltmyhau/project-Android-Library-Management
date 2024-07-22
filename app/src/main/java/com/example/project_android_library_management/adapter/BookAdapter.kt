package com.example.project_android_library_management.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.R
import com.example.project_android_library_management.model.Book

class BookAdapter(private val bookList: List<Book>) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        val tvStock: TextView = itemView.findViewById(R.id.tvStock)
        val imgBookCover: ImageView = itemView.findViewById(R.id.imgBookCover)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]
        holder.tvTitle.text = book.TenSach
        holder.tvAuthor.text = book.TacGia
        holder.tvStock.text = "Số lượng tồn: ${book.SoLuongTon}"
        holder.imgBookCover.setImageResource(R.drawable.book_cover)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }
}
