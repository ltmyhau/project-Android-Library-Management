package com.example.project_android_library_management.adapter

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.SendEmailTask
import com.example.project_android_library_management.dao.BorrowRecordDao
import com.example.project_android_library_management.dao.ReaderDao
import com.example.project_android_library_management.fragment.return_record.ReturnDetailActivity
import com.example.project_android_library_management.model.BorrowRecord
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RecordReaderAdapter(
    private val borrowRecordList: MutableList<BorrowRecord>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<RecordReaderAdapter.RecordReaderViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(borrowRecord: BorrowRecord)
    }

    inner class RecordReaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRecordID: TextView = itemView.findViewById(R.id.tvRecordID)
        val tvBorrowDate: TextView = itemView.findViewById(R.id.tvBorrowDate)
        val tvDueDate: TextView = itemView.findViewById(R.id.tvDueDate)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvShowDetail: TextView = itemView.findViewById(R.id.tvShowDetail)
        val tvSendEmail: TextView = itemView.findViewById(R.id.tvSendEmail)

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordReaderViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_record_reader, parent, false)
        return RecordReaderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecordReaderViewHolder, position: Int) {
        val borrowRecord = borrowRecordList[position]

        val readerDao = ReaderDao(DatabaseHelper(holder.itemView.context))
        val reader = readerDao.getReaderById(borrowRecord.MaDG)

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val borrowDate = formatter.parse(borrowRecord.NgayMuon) ?: return
        val calendar = Calendar.getInstance().apply {
            time = borrowDate
            add(Calendar.DAY_OF_YEAR, borrowRecord.SoNgayMuon)
        }

        val dueDate = calendar.time
        val currentDate = Calendar.getInstance().time

        val daysOverdue = if (currentDate.after(dueDate)) {
            val diffInMillis = currentDate.time - dueDate.time
            (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
        } else {
            0
        }

        holder.tvRecordID.text = borrowRecord.MaPM
        holder.tvBorrowDate.text = "Ngày mượn: ${borrowRecord.NgayMuon}"
        holder.tvDueDate.text = "Ngày đến hạn: ${formatter.format(dueDate)}"
        holder.tvShowDetail.paintFlags = holder.tvShowDetail.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        val borrowRecordDao = BorrowRecordDao(DatabaseHelper(holder.itemView.context))
        val status = borrowRecordDao.getStatusBorrowRecordById(borrowRecord.MaPM)
        when (status) {
            "Đã trả" -> {
                holder.tvStatus.text = status
                holder.tvStatus.setBackgroundResource(R.drawable.returned_background)
                holder.tvShowDetail.visibility = View.VISIBLE
                holder.tvSendEmail.visibility = View.GONE
            }

            "Chưa trả" -> {
                holder.tvStatus.text = status
                holder.tvStatus.setBackgroundResource(R.drawable.not_returned_background)
                holder.tvShowDetail.visibility = View.GONE
                holder.tvSendEmail.visibility = View.GONE
            }

            else -> {
                holder.tvStatus.text = status
                holder.tvStatus.setBackgroundResource(R.drawable.overdue_background)
                holder.tvShowDetail.visibility = View.GONE
                holder.tvSendEmail.visibility = View.VISIBLE
            }
        }

        holder.tvShowDetail.setOnClickListener {
            val intent = Intent(holder.itemView.context, ReturnDetailActivity::class.java)
            intent.putExtra("BORROW_ID", borrowRecord.MaPM)
            holder.itemView.context.startActivity(intent)
        }

        holder.tvSendEmail.setOnClickListener {
            if (reader != null) {
                val email = reader.Email
                val emailBody = """
                    Kính gửi Độc giả ${reader.HoTen},

                    Chúng tôi xin thông báo rằng bạn đã quá hạn trả sách tại thư viện Khoa Học Tổng Hợp Thành phố Hồ Chí Minh. Cụ thể:
                    - Mã phiếu mượn: ${borrowRecord.MaPM}
                    - Ngày mượn: ${borrowRecord.NgayMuon}
                    - Số ngày mượn: ${borrowRecord.SoNgayMuon}
                    - Ngày đến hạn: ${formatter.format(dueDate)}
                    - Số ngày quá hạn: ${daysOverdue} ngày
        
                    Theo quy định của thư viện, phí phạt cho mỗi ngày quá hạn là 2000 VND. Hiện tại, bạn đã quá hạn ${daysOverdue} ngày và tổng số tiền phạt là ${daysOverdue * 2000} VND.
                    
                    Để tránh phát sinh thêm phí phạt, vui lòng mang sách đến thư viện để trả sách và thanh toán phí phạt.
                    
                    Nếu có bất kỳ thắc mắc nào, xin vui lòng liên hệ với chúng tôi bằng cách phản hồi email này.
        
                    Trân trọng,
                    Thư viện Khoa Học Tổng Hợp Thành phố Hồ Chí Minh
                """.trimIndent()

                SendEmailTask(holder.itemView.context, email, "THÔNG BÁO QUÁ HẠN SÁCH", emailBody).execute()
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