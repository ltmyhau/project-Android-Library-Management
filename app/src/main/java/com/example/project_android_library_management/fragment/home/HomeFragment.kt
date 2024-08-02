package com.example.project_android_library_management.fragment.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.fragment.librarian.LibrarianFragment
import com.example.project_android_library_management.R
import com.example.project_android_library_management.dao.StatisticDao
import com.example.project_android_library_management.fragment.book.BookFragment
import com.example.project_android_library_management.fragment.borrow_record.BorrowRecordFragment
import com.example.project_android_library_management.fragment.order_book.OrderBookFragment
import com.example.project_android_library_management.fragment.reader.ReaderFragment
import com.example.project_android_library_management.fragment.return_record.ReturnRecordFragment
import com.example.project_android_library_management.fragment.statistic.StatisticFragment

class HomeFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private var userRole: String? = null
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var statisticDao: StatisticDao

    private lateinit var btnBook: Button
    private lateinit var btnOrderBook: Button
    private lateinit var btnBorrowRecord: Button
    private lateinit var btnReturnRecord: Button
    private lateinit var btnReader: Button
    private lateinit var btnLibrarian: Button
    private lateinit var btnStatistic: Button
    private lateinit var btnLibReader: Button
    private lateinit var btnLibStatistic: Button

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

        databaseHelper = DatabaseHelper(requireContext())
        statisticDao = StatisticDao(databaseHelper)

        btnBook = view.findViewById(R.id.btnBook)
        btnOrderBook = view.findViewById(R.id.btnOrderBook)
        btnBorrowRecord = view.findViewById(R.id.btnBorrowRecord)
        btnReturnRecord = view.findViewById(R.id.btnReturnRecord)
        btnReader = view.findViewById(R.id.btnReader)
        btnLibrarian = view.findViewById(R.id.btnLibrarian)
        btnStatistic = view.findViewById(R.id.btnStatistic)
        btnLibReader = view.findViewById(R.id.btnLibReader)
        btnLibStatistic = view.findViewById(R.id.btnLibStatistic)

        btnBook.setOnClickListener {
            listener?.onFragmentChange(BookFragment(), "Sách")
        }

        btnReader.setOnClickListener {
            listener?.onFragmentChange(ReaderFragment(), "Độc giả")
        }

        btnLibReader.setOnClickListener {
            listener?.onFragmentChange(ReaderFragment(), "Độc giả")
        }

        btnLibrarian.setOnClickListener {
            listener?.onFragmentChange(LibrarianFragment(), "Thủ thư")
        }

        btnLibStatistic.setOnClickListener {
            listener?.onFragmentChange(StatisticFragment(), "Thống kê")
        }

        btnOrderBook.setOnClickListener {
            listener?.onFragmentChange(OrderBookFragment(), "Đặt sách")
        }

        btnBorrowRecord.setOnClickListener {
            listener?.onFragmentChange(BorrowRecordFragment(), "Phiếu mượn")
        }

        btnReturnRecord.setOnClickListener {
            listener?.onFragmentChange(ReturnRecordFragment(), "Phiếu trả")
        }

        btnStatistic.setOnClickListener {
            listener?.onFragmentChange(StatisticFragment(), "Thống kê")
        }

        updateUI(view)

        return view
    }

    private fun updateUI(view: View) {
        btnBook.text = statisticDao.getBookQuantity().toString()
        btnOrderBook.text = statisticDao.getOrderBookQuantity().toString()
        btnBorrowRecord.text = statisticDao.getBorrowRecordQuantity().toString()
        btnReturnRecord.text = statisticDao.getReturnRecordQuantity().toString()
        btnReader.text = statisticDao.getReaderQuantity().toString()
        btnLibrarian.text = statisticDao.getLibrarianQuantity().toString()
        btnLibReader.text = statisticDao.getReaderQuantity().toString()

        val librarianRoleLayout = view.findViewById<LinearLayout>(R.id.librarianRoleLayout)
        val adminRole1Layout = view.findViewById<LinearLayout>(R.id.adminRole1Layout)
        val adminRole2Layout = view.findViewById<LinearLayout>(R.id.adminRole2Layout)
        if (userRole == "ThuThu") {
            librarianRoleLayout.visibility = View.VISIBLE
            adminRole1Layout.visibility = View.GONE
            adminRole2Layout.visibility = View.GONE
        } else {
            librarianRoleLayout.visibility = View.GONE
            adminRole1Layout.visibility = View.VISIBLE
            adminRole2Layout.visibility = View.VISIBLE
        }
    }

    fun setUserRole(role: String?) {
        userRole = role
        view?.let { updateUI(it) }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}