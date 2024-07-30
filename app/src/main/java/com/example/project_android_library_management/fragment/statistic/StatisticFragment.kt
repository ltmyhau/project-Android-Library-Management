package com.example.project_android_library_management.fragment.statistic

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.example.project_android_library_management.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatisticFragment : Fragment() {
    private lateinit var tabLayout: TabLayout
    private lateinit var switchBookReader: SwitchCompat
    private lateinit var tvSwitchBook: TextView
    private lateinit var tvSwitchReader: TextView
    private var selectedTab: String = "Ngày"
    private lateinit var edtFromDay: EditText
    private lateinit var edtToDay: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistic, container, false)

        tabLayout = view.findViewById(R.id.tabLayout)
        switchBookReader = view.findViewById(R.id.switchBookReader)
        tvSwitchBook = view.findViewById(R.id.tvSwitchBook)
        tvSwitchReader = view.findViewById(R.id.tvSwitchReader)

        tabLayout.addTab(tabLayout.newTab().setText("Ngày"))
        tabLayout.addTab(tabLayout.newTab().setText("Tháng"))
        tabLayout.addTab(tabLayout.newTab().setText("Quý"))
        tabLayout.addTab(tabLayout.newTab().setText("Năm"))
        tabLayout.addTab(tabLayout.newTab().setText("Tùy chỉnh"))

        val dateLayout: View = view.findViewById(R.id.dateLayout)
        dateLayout.visibility = View.GONE

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    selectedTab = tab.text.toString()
                    updateSelectedFragment(switchBookReader.isChecked)
                }
                when (tab?.text) {
                    "Tùy chỉnh" -> {
                        dateLayout.visibility = View.VISIBLE
                    }

                    else -> {
                        dateLayout.visibility = View.GONE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        val fromDateLayout: TextInputLayout = view.findViewById(R.id.fromDateLayout)
        edtFromDay = view.findViewById(R.id.edtFromDay)
        val toDateLayout: TextInputLayout = view.findViewById(R.id.toDateLayout)
        edtToDay = view.findViewById(R.id.edtToDay)
        fromDateLayout.setStartIconOnClickListener {
            showDatePickerDialog(edtFromDay)
            fromDateLayout.hint = ""
        }
        toDateLayout.setStartIconOnClickListener {
            showDatePickerDialog(edtToDay)
            toDateLayout.hint = ""
        }
        edtFromDay.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (edtFromDay.text.isEmpty()) {
                    fromDateLayout.hint = ""
                }
            } else {
                if (edtFromDay.text.isEmpty()) {
                    fromDateLayout.hint = "Từ ngày"
                }
            }
        }
        edtToDay.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (edtToDay.text.isEmpty()) {
                    toDateLayout.hint = ""
                }
            } else {
                if (edtToDay.text.isEmpty()) {
                    toDateLayout.hint = "Đến ngày"
                }
            }
        }

        switchBookReader.isChecked = false
        updateSwitchColors(false)
        switchBookReader.setOnCheckedChangeListener { _, isChecked  ->
            updateSwitchColors(isChecked)
            updateSelectedFragment(isChecked)
        }
        tvSwitchBook.setOnClickListener {
            switchBookReader.isChecked = !switchBookReader.isChecked
        }
        tvSwitchReader.setOnClickListener {
            switchBookReader.isChecked = !switchBookReader.isChecked
        }

        if (savedInstanceState == null) {
            updateSelectedFragment(switchBookReader.isChecked)
        }

        edtFromDay.addTextChangedListener(dateTextWatcher)
        edtToDay.addTextChangedListener(dateTextWatcher)

        return view
    }

    private fun updateSwitchColors(isChecked: Boolean) {
        tvSwitchBook.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (isChecked) R.color.primary else R.color.white
            )
        )
        tvSwitchReader.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (isChecked) R.color.white else R.color.primary
            )
        )
    }

    private fun updateSelectedFragment(isChecked: Boolean) {
        val fragment: Fragment = if (isChecked) {
            if (selectedTab == "Tùy chỉnh") {
                val fromDate = edtFromDay.text.toString()
                val toDate = edtToDay.text.toString()
                if (!areDatesValid(fromDate, toDate)) {
                    return
                }
                StatisticReaderFragment.newInstance(fromDate, toDate)
            } else {
                StatisticReaderFragment.newInstance(selectedTab)
            }
        } else {
            if (selectedTab == "Tùy chỉnh") {
                val fromDate = edtFromDay.text.toString()
                val toDate = edtToDay.text.toString()
                if (!areDatesValid(fromDate, toDate)) {
                    return
                }
                StatisticBookFragment.newInstance(fromDate, toDate)
            } else {
                StatisticBookFragment.newInstance(selectedTab)
            }
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun showDatePickerDialog(textView: TextView) {
        var calendar: Calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        try {
            dateFormat.parse(textView.text.toString())
            calendar.time = dateFormat.parse(textView.text.toString())!!
        } catch (e: ParseException) {
            calendar.time = Calendar.getInstance().time
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val formattedMonth = String.format("%02d", month + 1)
                val formattedDay = String.format("%02d", dayOfMonth)
                val selectedDate = "${year}-${formattedMonth}-${formattedDay}"
                textView.error = null
                textView.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private val dateTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (areDatesValid(edtFromDay.text.toString(), edtToDay.text.toString())) {
                updateSelectedFragment(switchBookReader.isChecked)
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun areDatesValid(fromDay: String, toDay: String): Boolean {
        if (fromDay.isEmpty() && toDay.isEmpty()) {
            edtFromDay.error = null
            edtToDay.error = null
            if (edtFromDay.hasFocus()) {
                edtFromDay.clearFocus()
            }
            if (edtToDay.hasFocus()) {
                edtToDay.clearFocus()
            }
            return true
        }

        if (fromDay.isNotEmpty()) {
            if (!validateDate(fromDay)) {
                edtFromDay.error = "Ngày (yyyy-MM-dd) không hợp lệ"
                edtFromDay.requestFocus()
                return false
            }
            if (toDay.isEmpty()) {
                edtToDay.error = "Ngày (yyyy-MM-dd) không được để trống"
                edtToDay.requestFocus()
                return false
            }
        }

        if (toDay.isNotEmpty()) {
            if (!validateDate(toDay)) {
                edtToDay.error = "Ngày (yyyy-MM-dd) không hợp lệ"
                edtToDay.requestFocus()
                return false
            }
            if (fromDay.isEmpty()) {
                edtFromDay.error = "Ngày (yyyy-MM-dd) không được để trống"
                edtFromDay.requestFocus()
                return false
            }
        }

        return true
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        isLenient = false
    }

    private fun validateDate(dateString: String): Boolean {
        return try {
            dateFormat.parse(dateString)
            true
        } catch (e: ParseException) {
            false
        }
    }
}