package com.example.project_android_library_management.fragment.reader

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.RecordReaderAdapter
import com.example.project_android_library_management.dao.BorrowRecordDao
import com.example.project_android_library_management.fragment.borrow_record.BorrowDetailActivity
import com.example.project_android_library_management.model.BorrowRecord

class RecordsByReaderFragment : Fragment() {
    private lateinit var rcvRecords: RecyclerView
    private lateinit var borrowRecordList: ArrayList<BorrowRecord>
    private lateinit var borrowRecordAdapter: RecordReaderAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var borrowRecordDao: BorrowRecordDao
    private lateinit var tvNoRecords: TextView

    private var status: Int? = null
    private var readerId: String? = null

    companion object {
        private const val REQUEST_CODE_BORROW_LIST = 1
        private const val ARG_STATUS = "status"
        private const val ARG_READER_ID = "reader_id"

        @JvmStatic
        fun newInstance(status: Int, readerId: String) = RecordsByReaderFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_STATUS, status)
                putString(ARG_READER_ID, readerId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            status = it.getInt(ARG_STATUS)
            readerId = it.getString(ARG_READER_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_records_by_reader, container, false)

        tvNoRecords = view.findViewById(R.id.tvNoRecords)
        rcvRecords = view.findViewById(R.id.rcvRecords)
        rcvRecords.layoutManager = LinearLayoutManager(context)
        rcvRecords.setHasFixedSize(true)

        databaseHelper = DatabaseHelper(requireContext())
        borrowRecordDao = BorrowRecordDao(databaseHelper)

        borrowRecordList = getRecordsByStatus(status, readerId)
        updateRecords()

        return view
    }

    override fun onResume() {
        super.onResume()
        borrowRecordList = getRecordsByStatus(status, readerId)
        updateRecords()
    }

    private fun updateRecords() {
        if (borrowRecordList.isEmpty()) {
            rcvRecords.visibility = View.GONE
            tvNoRecords.visibility = View.VISIBLE
        } else {
            rcvRecords.visibility = View.VISIBLE
            tvNoRecords.visibility = View.GONE

            if (rcvRecords.adapter == null) {
                borrowRecordAdapter = RecordReaderAdapter(borrowRecordList, object : RecordReaderAdapter.OnItemClickListener {
                    override fun onItemClick(borrowRecord: BorrowRecord) {
                        val intent = Intent(context, BorrowDetailActivity::class.java)
                        intent.putExtra("BORROW_ID", borrowRecord.MaPM)
                        startActivityForResult(intent, REQUEST_CODE_BORROW_LIST)
                    }
                })
                rcvRecords.adapter = borrowRecordAdapter
            } else {
                borrowRecordAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun getRecordsByStatus(status: Int?, readerId: String?): ArrayList<BorrowRecord> {
        val records = ArrayList<BorrowRecord>()
        val validReaderId = readerId ?: return records
        when (status) {
            0 -> records.addAll(borrowRecordDao.getPendingOrOverdueBorrowRecordsByReaderId(validReaderId))
            1 -> records.addAll(borrowRecordDao.getReturnedBorrowRecordsByReaderId(validReaderId))
        }
        Log.e("RECORD_LIST", records.toString())
        return records
    }
}