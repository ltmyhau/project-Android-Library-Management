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
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.R
import com.example.project_android_library_management.model.Reader
import java.io.File

class SearchReaderAdapter(
    private val context: Context,
    private val readerList: MutableList<Reader>,
) : RecyclerView.Adapter<SearchReaderAdapter.SearchReaderViewHolder>() {

    inner class SearchReaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
        val tvReaderName: TextView = itemView.findViewById(R.id.tvReaderName)
        val tvPhoneNumber: TextView = itemView.findViewById(R.id.tvPhoneNumber)
        val btnSelect: ImageView = itemView.findViewById(R.id.btnSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchReaderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_reader, parent, false)
        return SearchReaderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchReaderViewHolder, position: Int) {
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

        holder.btnSelect.setOnClickListener {
            val intent = Intent()
            intent.putExtra("READER_ID", reader.MaDG)
            (context as Activity).setResult(Activity.RESULT_OK, intent)
            (context as Activity).finish()
        }
    }

    override fun getItemCount(): Int {
        return readerList.size
    }
}