package com.example.project_android_library_management.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.R
import com.example.project_android_library_management.model.Book
import java.io.File

class BookByCategoryAdapter(private val bookList: ArrayList<Book>) : RecyclerView.Adapter<BookByCategoryAdapter.BookByCategoryViewHolder>() {

    inner class BookByCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgBookCover: ImageView = itemView.findViewById(R.id.imgBookCover)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookByCategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book_category, parent, false)
        return BookByCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookByCategoryViewHolder, position: Int) {
        val book = bookList[position]

        holder.tvTitle.text = book.TenSach

        if (book.HinhAnh != null) {
            val imgFile = File(book.HinhAnh)
            if (imgFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                holder.imgBookCover.setImageBitmap(bitmap)
            } else {
                holder.imgBookCover.setImageResource(R.drawable.book_cover)
            }
        } else {
            holder.imgBookCover.setImageResource(R.drawable.book_cover)
        }
    }

    override fun getItemCount(): Int {
        return bookList.size
    }
}