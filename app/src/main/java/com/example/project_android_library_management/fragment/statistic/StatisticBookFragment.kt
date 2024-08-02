package com.example.project_android_library_management.fragment.statistic

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.dao.StatisticDao
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatisticBookFragment : Fragment() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var statisticDao: StatisticDao
    private lateinit var statusCountsPieChart: PieChart
    private lateinit var bookMostBorrowedBarChart: BarChart
    private lateinit var bookCategoryBarChart: BarChart

    private lateinit var tvTotalBook: TextView
    private lateinit var tvBookBorrow: TextView
    private lateinit var tvBookReturn: TextView
    private lateinit var tvBookNew: TextView

    companion object {
        private const val ARG_TAB = "arg_tab"
        private const val ARG_FROM_DATE = "arg_from_date"
        private const val ARG_TO_DATE = "arg_to_date"

        fun newInstance(tab: String, fromDate: String? = null, toDate: String? = null): StatisticBookFragment {
            val fragment = StatisticBookFragment()
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
        val view = inflater.inflate(R.layout.fragment_statistic_book, container, false)

        databaseHelper = DatabaseHelper(requireContext())
        statisticDao = StatisticDao(databaseHelper)

        tvTotalBook = view.findViewById(R.id.tvTotalBook)
        tvBookBorrow = view.findViewById(R.id.tvBookBorrow)
        tvBookReturn = view.findViewById(R.id.tvBookReturn)
        tvBookNew = view.findViewById(R.id.tvBookNew)

        val selectedTab = arguments?.getString(ARG_TAB)
        var fromDate = arguments?.getString(ARG_FROM_DATE)
        var toDate = arguments?.getString(ARG_TO_DATE)

        val dates = when (selectedTab) {
            "Ngày" -> getTodayDates()
            "Tháng" -> getCurrentMonthDates()
            "Quý" -> getCurrentQuarterDates()
            "Năm" -> getCurrentYearDates()
            "Tùy chỉnh" -> null
            else -> null
        }
        if (dates != null) {
            fromDate = dates.first
            toDate = dates.second
        }

        loadQuantityBook(fromDate, toDate)

        statusCountsPieChart = view.findViewById(R.id.statusCountsPieChart)
        setupStatusCountsPieChart(fromDate, toDate)

        bookMostBorrowedBarChart = view.findViewById(R.id.bookMostBorrowedBarChart)
        setupBookMostBorrowBarChart(fromDate, toDate)

        bookCategoryBarChart = view.findViewById(R.id.bookCategoryBarChart)
        setupBookCategoryBarChart()

        return view
    }

    private fun loadQuantityBook(fromDate: String?, toDate: String?) {
        val totalBook = statisticDao.getTotalBook()
        val bookBorrow = statisticDao.getBookBorrow(fromDate, toDate)
        val bookReturn = statisticDao.getBookReturn(fromDate, toDate)
        val bookNew = statisticDao.getBookNew(fromDate, toDate)

        tvTotalBook.text = totalBook.toString()
        tvBookBorrow.text = bookBorrow.toString()
        tvBookReturn.text = bookReturn.toString()
        tvBookNew.text = bookNew.toString()
    }

    private fun setupStatusCountsPieChart(fromDate: String?, toDate: String?) {
        val statusCounts = statisticDao.getStatusCounts(fromDate, toDate)
        val entries = mutableListOf<PieEntry>()

        statusCounts.forEach { (status, count) ->
            entries.add(PieEntry(count.toFloat(), status))
        }

        if (entries.isEmpty()) {
            Toast.makeText(statusCountsPieChart.context, "Không có dữ liệu để hiển thị", Toast.LENGTH_SHORT).show()
            return
        }

        val dataSet = PieDataSet(entries, "")
        val colorList = listOf(
            ContextCompat.getColor(requireContext(), R.color.secondary),
            ContextCompat.getColor(requireContext(), R.color.secondary_variant),
            ContextCompat.getColor(requireContext(), R.color.third)
        )
        dataSet.colors = colorList

        val typeface = ResourcesCompat.getFont(requireContext(), R.font.oswald)
        dataSet.valueTypeface = typeface
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.text_dark_color)

        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val total = statusCountsPieChart.data?.getYValueSum() ?: 1f
                val percentage = (value / total) * 100
                return "${value.toInt()} (${String.format("%.2f", percentage)}%)"
            }
        }

        val pieData = PieData(dataSet)
        statusCountsPieChart.data = pieData

        customPieChart(statusCountsPieChart)

        statusCountsPieChart.invalidate()
    }

    private fun customPieChart(pieChart: PieChart) {
        pieChart.setHoleRadius(0f)
        pieChart.setTransparentCircleRadius(0f)
        pieChart.setHoleColor(Color.TRANSPARENT)

        pieChart.legend.apply {
            isEnabled = true
            form = Legend.LegendForm.SQUARE
            formSize = 10f
            textSize = 14f
            textColor = ContextCompat.getColor(requireContext(), R.color.text_dark_color)
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            setXEntrySpace(20f)
            setYEntrySpace(10f)
            setWordWrapEnabled(true)
        }

        pieChart.description.isEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.setDrawSliceText(false)
    }

    private fun setupBookMostBorrowBarChart(fromDate: String?, toDate: String?) {
        val bookMostBorrowed = statisticDao.getTop5BookMostBorrowed(fromDate, toDate)

        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        var index = 0
        bookMostBorrowed.forEach { (bookName, count) ->
            entries.add(BarEntry(index.toFloat(), count.toFloat()))
            labels.add(bookName)
            index++
        }

        val colorList = listOf(
            ContextCompat.getColor(requireContext(), R.color.primary_variant),
            ContextCompat.getColor(requireContext(), R.color.primary),
            ContextCompat.getColor(requireContext(), R.color.secondary),
            ContextCompat.getColor(requireContext(), R.color.secondary_variant),
            ContextCompat.getColor(requireContext(), R.color.third)
        )

        val dataSet = entries.mapIndexed { index, entry ->
            BarDataSet(listOf(entry), labels[index]).apply {
                colors = listOf(colorList[index])
                valueTextSize = 14f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }
                val typeface = ResourcesCompat.getFont(requireContext(), R.font.oswald)
                valueTypeface = typeface
            }
        }

        val barData = BarData(dataSet)
        bookMostBorrowedBarChart.data = barData

        customBarChart(bookMostBorrowedBarChart)

        bookMostBorrowedBarChart.invalidate()
    }

    private fun setupBookCategoryBarChart() {
        val bookCategoryData = statisticDao.getBookCategoryData()

        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        var index = 0
        bookCategoryData.forEach { (category, count) ->
            entries.add(BarEntry(index.toFloat(), count.toFloat()))
            labels.add(category)
            index++
        }

        val colorList = listOf(
            Color.parseColor("#a9d7e7"),
            Color.parseColor("#8cc2da"),
            Color.parseColor("#60a5c2"),
            Color.parseColor("#4790b1"),
            Color.parseColor("#2f7da3"),
            Color.parseColor("#2c6f99"),
            Color.parseColor("#044f89"),
            Color.parseColor("#03497c"),
            Color.parseColor("#033a61"),
            Color.parseColor("#002a4c"),
        )

        val dataSet = entries.mapIndexed { index, entry ->
            BarDataSet(listOf(entry), labels[index]).apply {
                colors = listOf(colorList[index])
                valueTextSize = 14f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }
                val typeface = ResourcesCompat.getFont(requireContext(), R.font.oswald)
                valueTypeface = typeface
            }
        }

        val barData = BarData(dataSet)
        bookCategoryBarChart.data = barData

        customBarChart(bookCategoryBarChart)

        bookCategoryBarChart.invalidate()
    }

    private fun customBarChart(barChart: BarChart) {
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return ""
                }
            }
            textSize = 12f
            setDrawGridLines(false)
        }

        barChart.axisLeft.apply {
            textSize = 12f
            setDrawGridLines(false)
        }

        barChart.axisRight.isEnabled = false

        barChart.legend.apply {
            isEnabled = true
            form = Legend.LegendForm.SQUARE
            formSize = 8f
            textSize = 12f
            textColor = ContextCompat.getColor(requireContext(), R.color.text_dark_color)
            setDrawInside(false)
            setWordWrapEnabled(true)
        }

        barChart.description.isEnabled = false
    }

    private fun getTodayDates(): Pair<String, String> {
        val today = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = formatter.format(today.time)
        return formattedDate to formattedDate
    }

    private fun getCurrentMonthDates(): Pair<String, String> {
        val today = Calendar.getInstance()
        val startOfMonth = today.clone() as Calendar
        val endOfMonth = today.clone() as Calendar
        startOfMonth.set(Calendar.DAY_OF_MONTH, 1)
        endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH))
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(startOfMonth.time) to formatter.format(endOfMonth.time)
    }

    private fun getCurrentQuarterDates(): Pair<String, String> {
        val today = Calendar.getInstance()
        val currentMonth = today.get(Calendar.MONTH) + 1
        val startMonth = when (currentMonth) {
            in 1..3 -> 0
            in 4..6 -> 3
            in 7..9 -> 6
            else -> 9
        }
        val endMonth = startMonth + 2
        val startOfQuarter = today.clone() as Calendar
        val endOfQuarter = today.clone() as Calendar
        startOfQuarter.set(Calendar.MONTH, startMonth)
        startOfQuarter.set(Calendar.DAY_OF_MONTH, 1)
        endOfQuarter.set(Calendar.MONTH, endMonth)
        endOfQuarter.set(Calendar.DAY_OF_MONTH, endOfQuarter.getActualMaximum(Calendar.DAY_OF_MONTH))
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(startOfQuarter.time) to formatter.format(endOfQuarter.time)
    }

    private fun getCurrentYearDates(): Pair<String, String> {
        val today = Calendar.getInstance()
        val currentYear = today.get(Calendar.YEAR)
        val startOfYear = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val endOfYear = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, Calendar.DECEMBER)
            set(Calendar.DAY_OF_MONTH, 31)
        }
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(startOfYear.time) to formatter.format(endOfYear.time)
    }
}