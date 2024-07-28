package com.example.project_android_library_management.fragment.order_book

import android.app.Activity
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
import com.example.project_android_library_management.adapter.OrderBookAdapter
import com.example.project_android_library_management.dao.OrderBookDao
import com.example.project_android_library_management.model.OrderBook
import com.google.android.material.floatingactionbutton.FloatingActionButton

class OrderBookFragment : Fragment() {
    private lateinit var rcvOrderBoooks: RecyclerView
    private lateinit var orderBookList: ArrayList<OrderBook>
    private lateinit var orderBookAdapter: OrderBookAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var orderBookDao: OrderBookDao

    companion object {
        private const val REQUEST_CODE_ORDER_LIST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_book, container, false)

        rcvOrderBoooks = view.findViewById(R.id.rcvOrderBoooks)
        rcvOrderBoooks.layoutManager = LinearLayoutManager(context)
        rcvOrderBoooks.setHasFixedSize(true)

        databaseHelper = DatabaseHelper(requireContext())
        orderBookDao = OrderBookDao(databaseHelper)
        orderBookList = orderBookDao.getAllOrderBook()

        orderBookAdapter = OrderBookAdapter(orderBookList, object : OrderBookAdapter.OnItemClickListener {
            override fun onItemClick(orderBook: OrderBook) {
                val intent = Intent(context, OrderDetailActivity::class.java)
                intent.putExtra("ORDER_ID", orderBook.MaPD)
                startActivityForResult(intent, REQUEST_CODE_ORDER_LIST)
            }
        })

        rcvOrderBoooks.adapter = orderBookAdapter

        val btnAdd = view.findViewById<FloatingActionButton>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val intent = Intent(activity, OrderAddActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ORDER_LIST && resultCode == Activity.RESULT_OK) {
            loadOrderBookList()
        }
    }

    override fun onResume() {
        super.onResume()
        loadOrderBookList()
    }

    private fun loadOrderBookList() {
        val orderBook = orderBookDao.getAllOrderBook()
        orderBookAdapter.updateData(orderBook)
    }
}