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
import com.example.project_android_library_management.dao.BorrowRecordDao
import com.example.project_android_library_management.dao.ReaderDao
import com.example.project_android_library_management.model.ReturnRecord
import java.io.File

class ReturnRecordAdapter(
    private val returnRecordList: MutableList<ReturnRecord>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ReturnRecordAdapter.ReturnRecordViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(borrowRecord: ReturnRecord)
    }

    inner class ReturnRecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
        val tvRecordID: TextView = itemView.findViewById(R.id.tvRecordID)
        val tvReaderName: TextView = itemView.findViewById(R.id.tvReaderName)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val btnSelect: ImageView = itemView.findViewById(R.id.btnSelect)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selected = returnRecordList[position]
                    itemClickListener.onItemClick(selected)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReturnRecordViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
        return ReturnRecordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReturnRecordViewHolder, position: Int) {
        val returnRecord = returnRecordList[position]

        val borrowRecordDao = BorrowRecordDao(DatabaseHelper(holder.itemView.context))
        val borrowRecord = borrowRecordDao.getBorrowRecordById(returnRecord.MaPM)

        val readerDao = ReaderDao(DatabaseHelper(holder.itemView.context))
        val reader = readerDao.getReaderById(borrowRecord?.MaDG)

        holder.tvRecordID.text = returnRecord.MaPT
        holder.tvDate.text = returnRecord.NgayTra

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

        holder.btnSelect.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return returnRecordList.size
    }

    fun updateData(newReturnRecordList: List<ReturnRecord>) {
        returnRecordList.clear()
        returnRecordList.addAll(newReturnRecordList)
        notifyDataSetChanged()
    }

}