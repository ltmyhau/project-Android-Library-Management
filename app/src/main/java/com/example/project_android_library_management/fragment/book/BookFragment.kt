package com.example.project_android_library_management.fragment.book

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.dao.BookCategoryDao
import com.example.project_android_library_management.dao.BookDao
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class BookFragment : Fragment() {
    private lateinit var edtSearch: SearchView
    private lateinit var bookListFragment: BookListFragment
    private lateinit var bookCategoryFragment: BookCategoryFragment
    private var currentTabPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book, container, false)

        edtSearch = view.findViewById(R.id.edtSearch)
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = view.findViewById<ViewPager2>(R.id.view_pager)

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

        return view
    }

    private inner class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
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
}