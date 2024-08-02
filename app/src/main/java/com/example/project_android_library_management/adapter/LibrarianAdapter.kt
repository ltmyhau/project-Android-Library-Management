package com.example.project_android_library_management.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.R
import com.example.project_android_library_management.model.Librarian
import java.io.File

class LibrarianAdapter(
    private val librarianList: MutableList<Librarian>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<LibrarianAdapter.LibrarianViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(librarian: Librarian)
    }

    inner class LibrarianViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
        val tvReaderName: TextView = itemView.findViewById(R.id.tvReaderName)
        val tvPhoneNumber: TextView = itemView.findViewById(R.id.tvPhoneNumber)
        val btnSelect: ImageView = itemView.findViewById(R.id.btnSelect)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selected = librarianList[position]
                    itemClickListener.onItemClick(selected)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibrarianViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_reader, parent, false)
        return LibrarianViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LibrarianViewHolder, position: Int) {
        val librarian = librarianList[position]
        holder.tvReaderName.text = librarian.HoTen
        holder.tvPhoneNumber.text = librarian.DienThoai

        if (librarian.HinhAnh != null) {
            val bitmap = BitmapFactory.decodeByteArray(librarian.HinhAnh, 0, librarian.HinhAnh.size)
            holder.imgAvatar.setImageBitmap(bitmap)
        } else {
            holder.imgAvatar.setImageResource(R.drawable.avatar)
        }

        holder.btnSelect.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return librarianList.size
    }

    fun updateData(newLibrarianList: List<Librarian>) {
        librarianList.clear()
        librarianList.addAll(newLibrarianList)
        notifyDataSetChanged()
    }

}