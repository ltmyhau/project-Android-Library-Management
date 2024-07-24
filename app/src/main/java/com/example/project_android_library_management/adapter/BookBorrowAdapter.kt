package com.example.project_android_library_management.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.dao.BookDao
import com.example.project_android_library_management.model.BorrowDetail
import java.io.File

class BookBorrowAdapter(
    private val borrowDetails: MutableList<BorrowDetail>
) : RecyclerView.Adapter<BookBorrowAdapter.BookBorrowViewHolder>() {

    inner class BookBorrowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgBookCover: ImageView = itemView.findViewById(R.id.imgBookCover)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        val btnDecrease: ImageButton = itemView.findViewById(R.id.btnDecrease)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val btnIncrease: ImageButton = itemView.findViewById(R.id.btnIncrease)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookBorrowViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_book_borrow, parent, false)
        return BookBorrowViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookBorrowViewHolder, position: Int) {
        val borrowDetail = borrowDetails[position]

        val bookDao = BookDao(DatabaseHelper(holder.itemView.context))
        val book = bookDao.getBookById(borrowDetail.MaSach)

        if (book != null) {
            holder.tvTitle.text = book.TenSach
            holder.tvAuthor.text = book.TacGia

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

        holder.tvQuantity.text = borrowDetail.SoLuong.toString()

//        holder.btnIncrease.setOnClickListener {
//            borrowDetail.SoLuong += 1
//            holder.tvQuantity.text = borrowDetail.SoLuong.toString()
//        }
//
//        holder.btnDecrease.setOnClickListener {
//            if (borrowDetail.SoLuong > 1) {
//                borrowDetail.SoLuong -= 1
//                holder.tvQuantity.text = borrowDetail.SoLuong.toString()
//            }
//        }

    }

    override fun getItemCount(): Int {
        return borrowDetails.size
    }
}