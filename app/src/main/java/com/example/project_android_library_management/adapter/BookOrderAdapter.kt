package com.example.project_android_library_management.adapter

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.text.Editable
import android.text.TextWatcher
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
import com.example.project_android_library_management.model.OrderDetail
import java.io.File

class BookOrderAdapter(
    private val orderDetails: MutableList<OrderDetail>
) : RecyclerView.Adapter<BookOrderAdapter.BookOrderViewHolder>() {

    private val viewHolders = mutableListOf<BookOrderViewHolder>()

    interface OnQuantityChangeListener {
        fun onQuantityChange()
    }

    private var quantityChangeListener: OnQuantityChangeListener? = null

    fun setOnQuantityChangeListener(listener: OnQuantityChangeListener) {
        quantityChangeListener = listener
    }

    inner class BookOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgBookCover: ImageView = itemView.findViewById(R.id.imgBookCover)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        val edtQuantity: TextView = itemView.findViewById(R.id.edtQuantity)
        val btnDecrease: ImageButton = itemView.findViewById(R.id.btnDecrease)
        val btnIncrease: ImageButton = itemView.findViewById(R.id.btnIncrease)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookOrderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_book_order, parent, false)
        return BookOrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookOrderViewHolder, position: Int) {
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

        holder.edtQuantity.setText(orderDetail.SoLuong.toString())

        holder.edtQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val newQuantity = s.toString().toIntOrNull()
                if (newQuantity != null && newQuantity > 0) {
                    orderDetail.SoLuong = newQuantity
                    holder.edtQuantity.error = null
                } else {
                    holder.edtQuantity.error = "Số lượng phải lớn hơn 0"
                }
                quantityChangeListener?.onQuantityChange()
            }
        })

        holder.btnIncrease.setOnClickListener {
            orderDetail.SoLuong += 1
            holder.edtQuantity.setText(orderDetail.SoLuong.toString())
            quantityChangeListener?.onQuantityChange()
        }

        holder.btnDecrease.setOnClickListener {
            if (orderDetail.SoLuong > 1) {
                orderDetail.SoLuong -= 1
                holder.edtQuantity.setText(orderDetail.SoLuong.toString())
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
        return orderDetails.size
    }

    private fun showDeleteConfirmationDialog(holder: BookOrderViewHolder, position: Int) {
        val context = holder.itemView.context
        AlertDialog.Builder(context)
            .setTitle("Xóa sách")
            .setMessage("Bạn có chắc chắn muốn xóa sách này không?")
            .setPositiveButton("Có") { _, _ ->
                if (orderDetails.size > 1){
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
        orderDetails.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, orderDetails.size)
    }
}