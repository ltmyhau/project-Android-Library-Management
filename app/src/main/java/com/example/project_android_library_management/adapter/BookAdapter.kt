package com.example.project_android_library_management.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.dao.BookDao
import com.example.project_android_library_management.model.Book
import com.example.project_android_library_management.model.BorrowDetail
import com.example.project_android_library_management.model.OrderDetail
import com.example.project_android_library_management.model.ReturnDetail
import java.io.File

class BookAdapter(
    private val bookList: MutableList<Book>?,
    private val borrowDetails: MutableList<BorrowDetail>?,
    private val returnDetails: MutableList<ReturnDetail>?,
    private val orderDetails: MutableList<OrderDetail>?,
    private val itemClickListener: OnItemClickListener?
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(book: Book)
    }

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val imgBookCover: ImageView = itemView.findViewById(R.id.imgBookCover)
        val btnAdd: ImageView = itemView.findViewById(R.id.btnAdd)

        init {
            if (bookList != null) {
                itemView.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val selected = bookList[position]
                        itemClickListener?.onItemClick(selected)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.btnAdd.visibility = View.GONE

        if (bookList != null) {
            val book = bookList[position]
            holder.tvTitle.text = book.TenSach
            holder.tvAuthor.text = book.TacGia
            holder.tvQuantity.text = "Số lượng tồn: ${book.SoLuongTon}"

            if (book.HinhAnh != null) {
                val bitmap = BitmapFactory.decodeByteArray(book.HinhAnh, 0, book.HinhAnh.size)
                holder.imgBookCover.setImageBitmap(bitmap)
            } else {
                holder.imgBookCover.setImageResource(R.drawable.book_cover)
            }
        } else if (borrowDetails != null) {
            val borrowDetail = borrowDetails[position]

            val bookDao = BookDao(DatabaseHelper(holder.itemView.context))
            val book = bookDao.getBookById(borrowDetail.MaSach)

            if (book != null) {
                holder.tvTitle.text = book.TenSach
                holder.tvAuthor.text = book.TacGia

                if (book.HinhAnh != null) {
                    val bitmap = BitmapFactory.decodeByteArray(book.HinhAnh, 0, book.HinhAnh.size)
                    holder.imgBookCover.setImageBitmap(bitmap)
                } else {
                    holder.imgBookCover.setImageResource(R.drawable.book_cover)
                }
            }
            holder.tvQuantity.text = "Số lượng: ${borrowDetail.SoLuong}"
        } else if (returnDetails != null) {
            val returnDetail = returnDetails[position]

            val bookDao = BookDao(DatabaseHelper(holder.itemView.context))
            val book = bookDao.getBookById(returnDetail.MaSach)

            if (book != null) {
                holder.tvTitle.text = book.TenSach
                holder.tvAuthor.text = book.TacGia

                if (book.HinhAnh != null) {
                    val bitmap = BitmapFactory.decodeByteArray(book.HinhAnh, 0, book.HinhAnh.size)
                    holder.imgBookCover.setImageBitmap(bitmap)
                } else {
                    holder.imgBookCover.setImageResource(R.drawable.book_cover)
                }
            }
            holder.tvQuantity.text = "Số lượng: ${returnDetail.SoLuong}"
        } else if (orderDetails != null) {
            val orderDetail = orderDetails[position]

            val bookDao = BookDao(DatabaseHelper(holder.itemView.context))
            val book = bookDao.getBookById(orderDetail.MaSach)

            if (book != null) {
                holder.tvTitle.text = book.TenSach
                holder.tvAuthor.text = book.TacGia

                if (book.HinhAnh != null) {
                    val bitmap = BitmapFactory.decodeByteArray(book.HinhAnh, 0, book.HinhAnh.size)
                    holder.imgBookCover.setImageBitmap(bitmap)
                } else {
                    holder.imgBookCover.setImageResource(R.drawable.book_cover)
                }
            }
            holder.tvQuantity.text = "Số lượng: ${orderDetail.SoLuong}"
        }
    }

    override fun getItemCount(): Int {
        return bookList?.size ?: borrowDetails?.size ?: returnDetails?.size ?: orderDetails?.size ?: 0
    }

    fun updateData(newBookList: List<Book>) {
        bookList?.clear()
        bookList?.addAll(newBookList)
        notifyDataSetChanged()
    }

}
