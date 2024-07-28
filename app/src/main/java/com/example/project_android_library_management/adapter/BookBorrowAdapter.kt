package com.example.project_android_library_management.adapter

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.dao.BookDao
import com.example.project_android_library_management.model.BorrowDetail
import java.io.File

class BookBorrowAdapter(
    private val borrowDetails: MutableList<BorrowDetail>
) : RecyclerView.Adapter<BookBorrowAdapter.BookBorrowViewHolder>() {

    interface OnQuantityChangeListener {
        fun onQuantityChange()
    }

    private var quantityChangeListener: OnQuantityChangeListener? = null

    fun setOnQuantityChangeListener(listener: OnQuantityChangeListener) {
        quantityChangeListener = listener
    }

    inner class BookBorrowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgBookCover: ImageView = itemView.findViewById(R.id.imgBookCover)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val btnDecrease: ImageButton = itemView.findViewById(R.id.btnDecrease)
        val btnIncrease: ImageButton = itemView.findViewById(R.id.btnIncrease)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
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

        holder.btnIncrease.setOnClickListener {
            borrowDetail.SoLuong += 1
            holder.tvQuantity.text = borrowDetail.SoLuong.toString()
            quantityChangeListener?.onQuantityChange()
        }

        holder.btnDecrease.setOnClickListener {
            if (borrowDetail.SoLuong > 1) {
                borrowDetail.SoLuong -= 1
                holder.tvQuantity.text = borrowDetail.SoLuong.toString()
                quantityChangeListener?.onQuantityChange()
            } else {
                showDeleteConfirmationDialog(holder, position)
            }
        }

        holder.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog(holder, position)
        }
    }

    override fun getItemCount(): Int {
        return borrowDetails.size
    }

    private fun showDeleteConfirmationDialog(holder: BookBorrowViewHolder, position: Int) {
        val context = holder.itemView.context
        AlertDialog.Builder(context)
            .setTitle("Xóa sách")
            .setMessage("Bạn có chắc chắn muốn xóa sách này không?")
            .setPositiveButton("Có") { _, _ ->
                if (borrowDetails.size > 1){
                    deleteItem(position)
                    quantityChangeListener?.onQuantityChange()
                    Toast.makeText(context, "Đã xóa sách thành công", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Không thể xóa sách cuối cùng", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Không", null)
            .show()
    }

    private fun deleteItem(position: Int) {
        borrowDetails.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, borrowDetails.size)
    }
}