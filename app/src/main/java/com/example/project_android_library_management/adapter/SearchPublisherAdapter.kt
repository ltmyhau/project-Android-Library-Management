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
import com.example.project_android_library_management.model.Publisher
import java.io.File

class SearchPublisherAdapter(
    private val context: Context,
    private val publisherList: MutableList<Publisher>,
) : RecyclerView.Adapter<SearchPublisherAdapter.SearchPublisherViewHolder>() {

    inner class SearchPublisherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
        val tvName: TextView = itemView.findViewById(R.id.tvReaderName)
        val tvPhoneNumber: TextView = itemView.findViewById(R.id.tvPhoneNumber)
        val btnSelect: ImageView = itemView.findViewById(R.id.btnSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchPublisherViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_reader, parent, false)
        return SearchPublisherViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchPublisherViewHolder, position: Int) {
        val publisher = publisherList[position]
        holder.tvName.text = publisher.TenNXB
        holder.tvPhoneNumber.text = publisher.DienThoai

        if (publisher.HinhAnh != null) {
            val bitmap = BitmapFactory.decodeByteArray(publisher.HinhAnh, 0, publisher.HinhAnh.size)
            holder.imgAvatar.setImageBitmap(bitmap)
        } else {
            holder.imgAvatar.setImageResource(R.drawable.avatar)
        }

        val clickListener = View.OnClickListener {
            val intent = Intent()
            intent.putExtra("PUBLISHER_ID", publisher.MaNXB)
            Toast.makeText(context, "Đã chọn ${publisher.TenNXB}", Toast.LENGTH_SHORT).show()
            (context as Activity).setResult(Activity.RESULT_OK, intent)
            (context as Activity).finish()
        }

        holder.btnSelect.setOnClickListener(clickListener)
        holder.itemView.setOnClickListener(clickListener)
    }

    override fun getItemCount(): Int {
        return publisherList.size
    }
}