package com.example.project_android_library_management.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.R
import com.example.project_android_library_management.model.Book
import java.io.File

class BookAdapter(
    private val bookList: MutableList<Book>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(book: Book)
    }

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        val tvStock: TextView = itemView.findViewById(R.id.tvStock)
        val imgBookCover: ImageView = itemView.findViewById(R.id.imgBookCover)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedBook = bookList[position]
                    itemClickListener.onItemClick(selectedBook)
                }
            }
        }
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
//        holder.imgBookCover.setImageResource(R.drawable.book_cover)
        if (book.HinhAnh != null) {
            val imgFile = File(book.HinhAnh)
            if (imgFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                holder.imgBookCover.setImageBitmap(bitmap)
            } else {
                holder.imgBookCover.setImageResource(R.drawable.book_cover) // Hình ảnh mặc định nếu file không tồn tại
            }
        } else {
            holder.imgBookCover.setImageResource(R.drawable.book_cover) // Hình ảnh mặc định nếu không có đường dẫn
        }
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    fun updateData(newBookList: List<Book>) {
        bookList.clear()
        bookList.addAll(newBookList)
        notifyDataSetChanged()
    }

}
