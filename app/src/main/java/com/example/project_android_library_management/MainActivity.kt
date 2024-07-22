package com.example.project_android_library_management

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.project_android_library_management.fragment.account.AccountFragment
import com.example.project_android_library_management.fragment.book.BookFragment
import com.example.project_android_library_management.fragment.borrow_record.BorrowRecordFragment
import com.example.project_android_library_management.fragment.home.HomeFragment
import com.example.project_android_library_management.fragment.reader.ReaderFragment
import com.example.project_android_library_management.fragment.return_record.ReturnRecordFragment
import com.example.project_android_library_management.fragment.statistic.StatisticFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment()).commit()
                toolbar.title = getString(R.string.app_name)
            }
            R.id.nav_book -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, BookFragment()).commit()
                toolbar.title = "Sách"
            }
            R.id.nav_reader -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ReaderFragment()).commit()
                toolbar.title = "Độc giả"
            }
            R.id.nav_borrow_record -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, BorrowRecordFragment()).commit()
                toolbar.title = "Phiếu mượn"
            }
            R.id.nav_return_record -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ReturnRecordFragment()).commit()
                toolbar.title = "Phiếu trả"
            }
            R.id.nav_statistic -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, StatisticFragment()).commit()
                toolbar.title = "Thống kê"
            }
            R.id.nav_account -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AccountFragment()).commit()
                toolbar.title = "Tài khoản"
            }
            R.id.nav_logout-> Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

//    override fun onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START)
//        } else {
//            onBackPressedDispatcher.onBackPressed()
//        }
//    }
}