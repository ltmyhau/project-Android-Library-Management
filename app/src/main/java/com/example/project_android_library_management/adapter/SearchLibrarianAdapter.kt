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
import com.example.project_android_library_management.model.Librarian
import java.io.File

class SearchLibrarianAdapter(
    private val context: Context,
    private val librarianList: MutableList<Librarian>,
) : RecyclerView.Adapter<SearchLibrarianAdapter.SearchLibrarianViewHolder>() {

    inner class SearchLibrarianViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
        val tvReaderName: TextView = itemView.findViewById(R.id.tvReaderName)
        val tvPhoneNumber: TextView = itemView.findViewById(R.id.tvPhoneNumber)
        val btnSelect: ImageView = itemView.findViewById(R.id.btnSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchLibrarianViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_reader, parent, false)
        return SearchLibrarianViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchLibrarianViewHolder, position: Int) {
        val librarian = librarianList[position]
        holder.tvReaderName.text = librarian.HoTen
        holder.tvPhoneNumber.text = librarian.DienThoai
        if (librarian.HinhAnh != null) {
            val imgFile = File(librarian.HinhAnh)
            if (imgFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                holder.imgAvatar.setImageBitmap(bitmap)
            } else {
                holder.imgAvatar.setImageResource(R.drawable.avatar)
            }
        } else {
            holder.imgAvatar.setImageResource(R.drawable.avatar)
        }

        val clickListener = View.OnClickListener {
            val intent = Intent()
            intent.putExtra("LIBRARIAN_ID", librarian.MaTT)
            Toast.makeText(context, "Đã chọn thủ thư ${librarian.HoTen}", Toast.LENGTH_SHORT).show()
            (context as Activity).setResult(Activity.RESULT_OK, intent)
            (context as Activity).finish()
        }

        holder.btnSelect.setOnClickListener(clickListener)
        holder.itemView.setOnClickListener(clickListener)
    }

    override fun getItemCount(): Int {
        return librarianList.size
    }
}