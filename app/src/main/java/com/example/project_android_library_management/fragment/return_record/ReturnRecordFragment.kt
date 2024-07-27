package com.example.project_android_library_management.fragment.return_record

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.ReturnRecordAdapter
import com.example.project_android_library_management.dao.ReturnRecordDao
import com.example.project_android_library_management.model.ReturnRecord
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ReturnRecordFragment : Fragment() {
    private lateinit var rcvReturnRecord: RecyclerView
    private lateinit var returncordList: ArrayList<ReturnRecord>
    private lateinit var returnRecordAdapter: ReturnRecordAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var returnRecordDao: ReturnRecordDao

    companion object {
        private const val REQUEST_CODE_RETURN_LIST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_return_record, container, false)

        rcvReturnRecord = view.findViewById(R.id.rcvReturnRecord)
        rcvReturnRecord.layoutManager = LinearLayoutManager(context)
        rcvReturnRecord.setHasFixedSize(true)

        databaseHelper = DatabaseHelper(requireContext())
        returnRecordDao = ReturnRecordDao(databaseHelper)
        returncordList = returnRecordDao.getAllReturnRecord()

        returnRecordAdapter = ReturnRecordAdapter(returncordList, object : ReturnRecordAdapter.OnItemClickListener {
            override fun onItemClick(returnRecord: ReturnRecord) {
//                val intent = Intent(context, ReturnDetailActivity::class.java)
//                intent.putExtra("RETURN_ID", returnRecord.MaPT)
//                startActivityForResult(intent, REQUEST_CODE_RETURN_LIST)
            }
        })

        rcvReturnRecord.adapter = returnRecordAdapter

        val btnAdd = view.findViewById<FloatingActionButton>(R.id.btnAdd)
        btnAdd.setOnClickListener {
//            val intent = Intent(activity, ReturnAddActivity::class.java)
//            startActivity(intent)
        }

        return view
    }
}