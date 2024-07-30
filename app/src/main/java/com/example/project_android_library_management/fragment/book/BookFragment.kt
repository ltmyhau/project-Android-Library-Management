package com.example.project_android_library_management.fragment.book

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.dao.BookCategoryDao
import com.example.project_android_library_management.dao.BookDao
import com.example.project_android_library_management.dao.PublisherDao
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class BookFragment : Fragment() {
    private lateinit var edtSearch: SearchView
    private lateinit var bookListFragment: BookListFragment
    private lateinit var bookCategoryFragment: BookCategoryFragment
    private var currentTabPosition = 0

    private lateinit var btnFilter: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnClose: ImageView
    private lateinit var spnPublisher: AutoCompleteTextView
    private lateinit var edtFromYear: EditText
    private lateinit var edtToYear: EditText
    private lateinit var edtAuthor: EditText
    private lateinit var btnReset: Button
    private lateinit var btnApply: Button

    private var publisherId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book, container, false)

        edtSearch = view.findViewById(R.id.edtSearch)
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = view.findViewById<ViewPager2>(R.id.view_pager)

        btnFilter = view.findViewById(R.id.btnFilter)
        drawerLayout = view.findViewById(R.id.drawer_layout)
        navView = view.findViewById(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        btnClose = headerView.findViewById(R.id.btnClose)
        spnPublisher = headerView.findViewById(R.id.spnPublisher)
        edtFromYear = headerView.findViewById(R.id.edtFromYear)
        edtToYear = headerView.findViewById(R.id.edtToYear)
        edtAuthor = headerView.findViewById(R.id.edtAuthor)
        btnReset = headerView.findViewById(R.id.btnReset)
        btnApply = headerView.findViewById(R.id.btnApply)

        val pagerAdapter = ViewPagerAdapter(requireActivity())
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Sách"
                1 -> "Loại sách"
                else -> null
            }
        }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentTabPosition = position
                edtSearch.setQuery("", false)
                resetFilter()

                if (position == 1) {
                    btnFilter.visibility = View.GONE
                } else {
                    btnFilter.visibility = View.VISIBLE
                }
            }
        })

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

        btnFilter.setOnClickListener {
            drawerLayout.openDrawer(navView)
            loadPublisherSpinner()
        }

        btnClose.setOnClickListener {
            drawerLayout.closeDrawer(navView)
            resetFilter()
            applyFilter()
        }

        btnReset.setOnClickListener {
            resetFilter()
        }

        btnApply.setOnClickListener {
            applyFilter()
        }

        return view
    }

    private inner class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> BookListFragment().also { bookListFragment = it }
                1 -> BookCategoryFragment().also { bookCategoryFragment = it }
                else -> throw IllegalStateException("Invalid position $position")
            }
        }
    }

    private fun performSearch(query: String) {
        if (currentTabPosition == 0) {
            val bookDao = BookDao(DatabaseHelper(requireContext()))
            val listSearch = bookDao.searchBook(query)
            if (::bookListFragment.isInitialized) {
                bookListFragment.updateBookList(listSearch)
            }
        } else if (currentTabPosition == 1) {
            val bookCategoryDao = BookCategoryDao(DatabaseHelper(requireContext()))
            val listSearch = bookCategoryDao.searchBookCategories(query)
            if (::bookCategoryFragment.isInitialized) {
                bookCategoryFragment.updateCategoryList(listSearch)
            }
        }
    }

    private fun loadPublisherSpinner() {
        val publisherDao = PublisherDao(DatabaseHelper(requireContext()))
        val publishers = publisherDao.getAllPublisher()
        val publisherArray = publishers.map { it.TenNXB }.toTypedArray()
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, publisherArray)
        spnPublisher.setAdapter(adapter)

        spnPublisher.setOnItemClickListener { parent, _, position, _ ->
            publisherId = publishers[position].MaNXB
        }
    }

    private fun applyFilter() {
        val fromYear = edtFromYear.text.toString().toIntOrNull()
        val toYear = edtToYear.text.toString().toIntOrNull()
        val author = edtAuthor.text.toString()
        val bookDao = BookDao(DatabaseHelper(requireContext()))
        val listSearch = bookDao.searchBookByFilter(publisherId, fromYear, toYear, author)
        if (::bookListFragment.isInitialized) {
            bookListFragment.updateBookList(listSearch)
        }
        drawerLayout.closeDrawer(navView)
    }

    private fun resetFilter() {
        publisherId = ""
        spnPublisher.setText("Nhà xuất bản")
        edtFromYear.text.clear()
        edtToYear.text.clear()
        edtAuthor.text.clear()
    }
}