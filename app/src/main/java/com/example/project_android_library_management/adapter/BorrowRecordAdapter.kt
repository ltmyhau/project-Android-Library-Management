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
import com.example.project_android_library_management.dao.ReaderDao
import com.example.project_android_library_management.model.BorrowRecord
import java.io.File

class BorrowRecordAdapter(
    private val borrowRecordList: MutableList<BorrowRecord>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<BorrowRecordAdapter.BorrowRecordViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(borrowRecord: BorrowRecord)
    }

    inner class BorrowRecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
        val tvBorrowRecordID: TextView = itemView.findViewById(R.id.tvBorrowRecordID)
        val tvReaderName: TextView = itemView.findViewById(R.id.tvReaderName)
        val tvBorrowDate: TextView = itemView.findViewById(R.id.tvBorrowDate)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selected = borrowRecordList[position]
                    itemClickListener.onItemClick(selected)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowRecordViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_borrow_record, parent, false)
        return BorrowRecordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BorrowRecordViewHolder, position: Int) {
        val borrowRecord = borrowRecordList[position]

        val readerDao = ReaderDao(DatabaseHelper(holder.itemView.context))
        val reader = readerDao.getReaderById(borrowRecord.MaDG)

        holder.tvBorrowRecordID.text = borrowRecord.MaPM
        holder.tvBorrowDate.text = borrowRecord.NgayMuon

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
    }

    override fun getItemCount(): Int {
        return borrowRecordList.size
    }

    fun updateData(newBorrowRecordList: List<BorrowRecord>) {
        borrowRecordList.clear()
        borrowRecordList.addAll(newBorrowRecordList)
        notifyDataSetChanged()
    }

}