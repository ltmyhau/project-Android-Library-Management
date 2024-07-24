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
import com.example.project_android_library_management.model.Reader
import java.io.File

class ReaderAdapter(
    private val readerList: MutableList<Reader>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ReaderAdapter.ReaderViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(reader: Reader)
    }

    inner class ReaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
        val tvReaderName: TextView = itemView.findViewById(R.id.tvReaderName)
        val tvPhoneNumber: TextView = itemView.findViewById(R.id.tvPhoneNumber)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selected = readerList[position]
                    itemClickListener.onItemClick(selected)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReaderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_reader, parent, false)
        return ReaderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReaderViewHolder, position: Int) {
        val reader = readerList[position]
        holder.tvReaderName.text = reader.HoTen
        holder.tvPhoneNumber.text = reader.DienThoai
        if (reader.HinhAnh != null) {
            val imgFile = File(reader.HinhAnh)
            if (imgFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                holder.imgAvatar.setImageBitmap(bitmap)
            } else {
                holder.imgAvatar.setImageResource(R.drawable.avatar)
            }
        } else {
            holder.imgAvatar.setImageResource(R.drawable.avatar)
        }
    }

    override fun getItemCount(): Int {
        return readerList.size
    }

    fun updateData(newReaderList: List<Reader>) {
        readerList.clear()
        readerList.addAll(newReaderList)
        notifyDataSetChanged()
    }

}