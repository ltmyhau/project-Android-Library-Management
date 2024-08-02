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
import com.example.project_android_library_management.dao.BorrowDetailDao
import com.example.project_android_library_management.model.ReturnDetail
import java.io.File

class BookReturnAdapter(
    private val returnDetails: MutableList<ReturnDetail>,
    private val borrowId: String
) : RecyclerView.Adapter<BookReturnAdapter.BookReturnViewHolder>() {

    interface OnQuantityChangeListener {
        fun onQuantityChange()
    }

    private var quantityChangeListener: OnQuantityChangeListener? = null

    fun setOnQuantityChangeListener(listener: OnQuantityChangeListener) {
        quantityChangeListener = listener
    }

    inner class BookReturnViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgBookCover: ImageView = itemView.findViewById(R.id.imgBookCover)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvBorrowQuantity: TextView = itemView.findViewById(R.id.tvBorrowQuantity)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val btnDecrease: ImageButton = itemView.findViewById(R.id.btnDecrease)
        val btnIncrease: ImageButton = itemView.findViewById(R.id.btnIncrease)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookReturnViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_book_return, parent, false)
        return BookReturnViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookReturnViewHolder, position: Int) {
        val returnDetail = returnDetails[position]
        var borrowQuantity = 0

        val borrowDetailDao = BorrowDetailDao(DatabaseHelper(holder.itemView.context))
        val borrowDetail = borrowDetailDao.getBorrowDetailByIdAndBookId(borrowId, returnDetail.MaSach)

        if (borrowDetail != null) {
            borrowQuantity = borrowDetail.SoLuong
            holder.tvBorrowQuantity.text = "Số lượng mượn: ${borrowQuantity}"
        }

        val bookDao = BookDao(DatabaseHelper(holder.itemView.context))
        val book = bookDao.getBookById(returnDetail.MaSach)

        if (book != null) {
            holder.tvTitle.text = book.TenSach

            if (book.HinhAnh != null) {
                val bitmap = BitmapFactory.decodeByteArray(book.HinhAnh, 0, book.HinhAnh.size)
                holder.imgBookCover.setImageBitmap(bitmap)
            } else {
                holder.imgBookCover.setImageResource(R.drawable.book_cover)
            }
        }

        holder.tvQuantity.text = returnDetail.SoLuong.toString()

        holder.btnIncrease.setOnClickListener {
            if (returnDetail.SoLuong < borrowQuantity) {
                returnDetail.SoLuong += 1
                holder.tvQuantity.text = returnDetail.SoLuong.toString()
                quantityChangeListener?.onQuantityChange()
            } else {
                Toast.makeText(holder.itemView.context, "Số lượng trả không được lớn hơn số lượng mượn", Toast.LENGTH_SHORT).show()
            }
        }

        holder.btnDecrease.setOnClickListener {
            if (returnDetail.SoLuong > 1) {
                returnDetail.SoLuong -= 1
                holder.tvQuantity.text = returnDetail.SoLuong.toString()
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
        return returnDetails.size
    }

    private fun showDeleteConfirmationDialog(holder: BookReturnViewHolder, position: Int) {
        val context = holder.itemView.context
        AlertDialog.Builder(context)
            .setTitle("Xóa sách")
            .setMessage("Bạn có chắc chắn muốn xóa sách này không?")
            .setPositiveButton("Có") { _, _ ->
                if (returnDetails.size > 1){
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
        returnDetails.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, returnDetails.size)
    }
}