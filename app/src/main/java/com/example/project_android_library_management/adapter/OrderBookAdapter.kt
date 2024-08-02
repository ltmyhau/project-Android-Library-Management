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
import com.example.project_android_library_management.dao.PublisherDao
import com.example.project_android_library_management.model.OrderBook
import java.io.File

class OrderBookAdapter(
    private val orderBookList: MutableList<OrderBook>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<OrderBookAdapter.OrderBookViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(orderBook: OrderBook)
    }

    inner class OrderBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
        val tvOrderID: TextView = itemView.findViewById(R.id.tvRecordID)
        val tvPublisher: TextView = itemView.findViewById(R.id.tvReaderName)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val btnSelect: ImageView = itemView.findViewById(R.id.btnSelect)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selected = orderBookList[position]
                    itemClickListener.onItemClick(selected)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderBookViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
        return OrderBookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderBookViewHolder, position: Int) {
        val orderBook = orderBookList[position]

        val publisherDao = PublisherDao(DatabaseHelper(holder.itemView.context))
        val publisher = publisherDao.getPublisherById(orderBook.MaNXB)

        holder.tvOrderID.text = orderBook.MaPD
        holder.tvDate.text = orderBook.NgayDat
        holder.tvPublisher.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_publisher_16, 0, 0, 0)

        if (publisher != null) {
            holder.tvPublisher.text = publisher.TenNXB

            if (publisher.HinhAnh != null) {
                val bitmap = BitmapFactory.decodeByteArray(publisher.HinhAnh, 0, publisher.HinhAnh.size)
                holder.imgAvatar.setImageBitmap(bitmap)
            } else {
                holder.imgAvatar.setImageResource(R.drawable.avatar)
            }
        }

        holder.btnSelect.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return orderBookList.size
    }

    fun updateData(newOrderBookList: List<OrderBook>) {
        orderBookList.clear()
        orderBookList.addAll(newOrderBookList)
        notifyDataSetChanged()
    }

}