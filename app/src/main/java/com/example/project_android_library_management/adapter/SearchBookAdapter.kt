package com.example.project_android_library_management.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.R
import com.example.project_android_library_management.model.Book
import java.io.File

class SearchBookAdapter(
    private val context: Context,
    private val bookList: MutableList<Book>,
) : RecyclerView.Adapter<SearchBookAdapter.SearchBookViewHolder>() {

    inner class SearchBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val imgBookCover: ImageView = itemView.findViewById(R.id.imgBookCover)
        val btnAdd: ImageView = itemView.findViewById(R.id.btnAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchBookViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return SearchBookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchBookViewHolder, position: Int) {
        val book = bookList[position]
        holder.tvTitle.text = book.TenSach
        holder.tvAuthor.text = book.TacGia
        holder.tvQuantity.text = "Số lượng tồn: ${book.SoLuongTon}"
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

        holder.btnAdd.setOnClickListener {
            val intent = Intent()
            intent.putExtra("BOOK_ID", book.MaSach)
            Toast.makeText(context, "Đã thêm sách ${book.TenSach}", Toast.LENGTH_SHORT).show()
            (context as Activity).setResult(Activity.RESULT_OK, intent)
            (context as Activity).finish()
        }
    }

    override fun getItemCount(): Int {
        return bookList.size
    }
}