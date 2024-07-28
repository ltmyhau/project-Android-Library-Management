package com.example.project_android_library_management.fragment.borrow_record

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.BorrowRecordAdapter
import com.example.project_android_library_management.dao.BorrowRecordDao
import com.example.project_android_library_management.model.BorrowRecord
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BorrowRecordFragment : Fragment() {
    private lateinit var rcvBorrowRecord: RecyclerView
    private lateinit var borrowRecordList: ArrayList<BorrowRecord>
    private lateinit var borrowRecordAdapter: BorrowRecordAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var borrowRecordDao: BorrowRecordDao
    private lateinit var edtSearch: SearchView

    companion object {
        private const val REQUEST_CODE_BORROW_LIST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_borrow_record, container, false)

        edtSearch = view.findViewById(R.id.edtSearch)
        rcvBorrowRecord = view.findViewById(R.id.rcvBorrowRecord)
        rcvBorrowRecord.layoutManager = LinearLayoutManager(context)
        rcvBorrowRecord.setHasFixedSize(true)

        databaseHelper = DatabaseHelper(requireContext())
        borrowRecordDao = BorrowRecordDao(databaseHelper)
        borrowRecordList = borrowRecordDao.getAllBorrowRecord()

        borrowRecordAdapter = BorrowRecordAdapter(borrowRecordList, object : BorrowRecordAdapter.OnItemClickListener {
            override fun onItemClick(borrowRecord: BorrowRecord) {
                val intent = Intent(context, BorrowDetailActivity::class.java)
                intent.putExtra("BORROW_ID", borrowRecord.MaPM)
                startActivityForResult(intent, REQUEST_CODE_BORROW_LIST)
            }
        })

        rcvBorrowRecord.adapter = borrowRecordAdapter

        val btnAdd = view.findViewById<FloatingActionButton>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val intent = Intent(activity, BorrowAddActivity::class.java)
            startActivity(intent)
        }

        edtSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    performSearch(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    performSearch(it)
                }
                return true
            }
        })

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_BORROW_LIST && resultCode == Activity.RESULT_OK) {
            loadBorrowRecordList()
        }
    }

    override fun onResume() {
        super.onResume()
        loadBorrowRecordList()
    }

    private fun loadBorrowRecordList() {
        val borrowRecord = borrowRecordDao.getAllBorrowRecord()
        borrowRecordAdapter.updateData(borrowRecord)
    }

    fun updateBorrowRecordList(newList: List<BorrowRecord>) {
        borrowRecordList.clear()
        borrowRecordList.addAll(newList)
        borrowRecordAdapter.notifyDataSetChanged()
    }

    private fun performSearch(query: String) {
        val listSearch = borrowRecordDao.searchBorrowRecord(query)
        updateBorrowRecordList(listSearch)
    }
}