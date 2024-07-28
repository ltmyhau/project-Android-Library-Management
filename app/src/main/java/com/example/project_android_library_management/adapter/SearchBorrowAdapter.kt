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
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.dao.ReaderDao
import com.example.project_android_library_management.model.BorrowRecord
import java.io.File

class SearchBorrowAdapter (
    private val context: Context,
    private val borrowRecordList: MutableList<BorrowRecord>,
) : RecyclerView.Adapter<SearchBorrowAdapter.SearchBorrowViewHolder>() {

    inner class SearchBorrowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
        val tvRecordID: TextView = itemView.findViewById(R.id.tvRecordID)
        val tvReaderName: TextView = itemView.findViewById(R.id.tvReaderName)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val btnSelect: ImageView = itemView.findViewById(R.id.btnSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchBorrowViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
        return SearchBorrowViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchBorrowViewHolder, position: Int) {
        val borrowRecord = borrowRecordList[position]

        val readerDao = ReaderDao(DatabaseHelper(holder.itemView.context))
        val reader = readerDao.getReaderById(borrowRecord.MaDG)

        holder.tvRecordID.text = borrowRecord.MaPM
        holder.tvDate.text = borrowRecord.NgayMuon

        if (reader != null) {
            holder.tvReaderName.text = reader.HoTen

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

        holder.btnSelect.setOnClickListener {
            val intent = Intent()
            intent.putExtra("BORROW_ID", borrowRecord.MaPM)
            Toast.makeText(context, "Đã chọn phiếu mượn ${borrowRecord.MaPM}", Toast.LENGTH_SHORT).show()
            (context as Activity).setResult(Activity.RESULT_OK, intent)
            (context as Activity).finish()
        }
    }

    override fun getItemCount(): Int {
        return borrowRecordList.size
    }
}