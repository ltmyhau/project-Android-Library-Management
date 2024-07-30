package com.example.project_android_library_management.fragment.statistic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.project_android_library_management.R

class StatisticReaderFragment : Fragment() {

    companion object {
        private const val ARG_TAB = "arg_tab"
        private const val ARG_FROM_DATE = "arg_from_date"
        private const val ARG_TO_DATE = "arg_to_date"

        fun newInstance(tab: String, fromDate: String? = null, toDate: String? = null): StatisticReaderFragment {
            val fragment = StatisticReaderFragment()
            val args = Bundle().apply {
                putString(ARG_TAB, tab)
                putString(ARG_FROM_DATE, fromDate)
                putString(ARG_TO_DATE, toDate)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistic_reader, container, false)

        val selectedTab = arguments?.getString(ARG_TAB)
        val fromDate = arguments?.getString(ARG_FROM_DATE)
        val toDate = arguments?.getString(ARG_TO_DATE)

        return view
    }
}