package com.example.project_android_library_management.fragment.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.project_android_library_management.R
import com.example.project_android_library_management.fragment.book.BookFragment
import com.example.project_android_library_management.fragment.borrow_record.BorrowRecordFragment
import com.example.project_android_library_management.fragment.order_book.OrderBookFragment
import com.example.project_android_library_management.fragment.reader.ReaderFragment
import com.example.project_android_library_management.fragment.return_record.ReturnRecordFragment
import com.example.project_android_library_management.fragment.statistic.StatisticFragment

class HomeFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null

    interface OnFragmentInteractionListener {
        fun onFragmentChange(fragment: Fragment, title: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val btnBook = view.findViewById<Button>(R.id.btnBook)
        val btnReader = view.findViewById<Button>(R.id.btnReader)
        val btnBorrowRecord = view.findViewById<Button>(R.id.btnBorrowRecord)
        val btnReturnRecord = view.findViewById<Button>(R.id.btnReturnRecord)
        val btnOrderBook = view.findViewById<Button>(R.id.btnOrderBook)
        val btnStatistic = view.findViewById<Button>(R.id.btnStatistic)

        btnBook.setOnClickListener {
            listener?.onFragmentChange(BookFragment(), "Sách")
        }

        btnReader.setOnClickListener {
            listener?.onFragmentChange(ReaderFragment(), "Độc giả")
        }

        btnBorrowRecord.setOnClickListener {
            listener?.onFragmentChange(BorrowRecordFragment(), "Phiếu mượn")
        }

        btnReturnRecord.setOnClickListener {
            listener?.onFragmentChange(ReturnRecordFragment(), "Phiếu trả")
        }

        btnOrderBook.setOnClickListener {
            listener?.onFragmentChange(OrderBookFragment(), "Đặt sách")
        }

        btnStatistic.setOnClickListener {
            listener?.onFragmentChange(StatisticFragment(), "Thống kê")
        }

        return view
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}