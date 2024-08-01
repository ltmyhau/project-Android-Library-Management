package com.example.project_android_library_management

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.project_android_library_management.fragment.account.AccountFragment
import com.example.project_android_library_management.fragment.book.BookFragment
import com.example.project_android_library_management.fragment.borrow_record.BorrowRecordFragment
import com.example.project_android_library_management.fragment.home.HomeFragment
import com.example.project_android_library_management.fragment.librarian.LibrarianFragment
import com.example.project_android_library_management.fragment.order_book.OrderBookFragment
import com.example.project_android_library_management.fragment.reader.ReaderFragment
import com.example.project_android_library_management.fragment.return_record.ReturnRecordFragment
import com.example.project_android_library_management.fragment.statistic.StatisticFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private var userRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        userRole = intent.getStringExtra("USER_ROLE")
        configureDrawerMenu(userRole)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            val homeFragment = HomeFragment()
            homeFragment.setUserRole(userRole)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, homeFragment).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    private fun configureDrawerMenu(role: String?) {
        val menu = navigationView.menu
        menu.findItem(R.id.nav_librarians).isVisible = (role == "Admin")
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var selectedFragment: Fragment? = null
        var title = ""

        when (item.itemId) {
            R.id.nav_home -> {
                selectedFragment = HomeFragment().apply {
                    setUserRole(userRole)
                }
                title = getString(R.string.app_name)
            }
            R.id.nav_books -> {
                selectedFragment = BookFragment()
                title = "Sách"
            }
            R.id.nav_readers -> {
                selectedFragment = ReaderFragment()
                title = "Độc giả"
            }
            R.id.nav_librarians -> {
                selectedFragment = LibrarianFragment()
                title = "Thủ thư"
            }
            R.id.nav_borrow_record -> {
                selectedFragment = BorrowRecordFragment()
                title = "Phiếu mượn"
            }
            R.id.nav_return_record -> {
                selectedFragment = ReturnRecordFragment()
                title = "Phiếu trả"
            }
            R.id.nav_order_book -> {
                selectedFragment = OrderBookFragment()
                title = "Đặt sách"
            }
            R.id.nav_statistics -> {
                selectedFragment = StatisticFragment()
                title = "Thống kê"
            }
            R.id.nav_account -> {
                selectedFragment = AccountFragment()
                title = "Tài khoản"
            }
            R.id.nav_logout -> {
                Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        if (selectedFragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit()
            toolbar.title = title
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onFragmentChange(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
        supportActionBar?.title = title

        when (title) {
            "Sách" -> navigationView.setCheckedItem(R.id.nav_books)
            "Độc giả" -> navigationView.setCheckedItem(R.id.nav_readers)
            "Thủ thư" -> navigationView.setCheckedItem(R.id.nav_librarians)
            "Phiếu mượn" -> navigationView.setCheckedItem(R.id.nav_borrow_record)
            "Phiếu trả" -> navigationView.setCheckedItem(R.id.nav_return_record)
            "Đặt sách" -> navigationView.setCheckedItem(R.id.nav_order_book)
            "Thống kê" -> navigationView.setCheckedItem(R.id.nav_statistics)
            else -> navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}