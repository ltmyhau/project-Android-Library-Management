package com.example.project_android_library_management.fragment.reader

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.adapter.ReaderAdapter
import com.example.project_android_library_management.dao.ReaderDao
import com.example.project_android_library_management.fragment.book.BookAddActivity
import com.example.project_android_library_management.model.Reader
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ReaderFragment : Fragment() {
    private lateinit var rcvReaders: RecyclerView
    private lateinit var readerList: ArrayList<Reader>
    private lateinit var readerAdapter: ReaderAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var readerDao: ReaderDao

    companion object {
        private const val REQUEST_CODE_READER_LIST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reader, container, false)

        rcvReaders = view.findViewById(R.id.rcvReaders)
        rcvReaders.layoutManager = LinearLayoutManager(context)
        rcvReaders.setHasFixedSize(true)

        databaseHelper = DatabaseHelper(requireContext())
        readerDao = ReaderDao(databaseHelper)
        readerList = readerDao.getAllReaders()

        readerAdapter = ReaderAdapter(readerList, object : ReaderAdapter.OnItemClickListener {
            override fun onItemClick(reader: Reader) {
                val intent = Intent(context, ReaderDetailActivity::class.java)
                intent.putExtra("READER_ID", reader.MaDG)
                startActivityForResult(intent, REQUEST_CODE_READER_LIST)
            }
        })

        rcvReaders.adapter = readerAdapter

        val btnAdd = view.findViewById<FloatingActionButton>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val intent = Intent(activity, ReaderAddActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_READER_LIST && resultCode == Activity.RESULT_OK) {
            loadReaderList()
        }
    }

    override fun onResume() {
        super.onResume()
        loadReaderList()
    }

    private fun loadReaderList() {
        val readers = readerDao.getAllReaders()
        readerAdapter.updateData(readers)
    }
}