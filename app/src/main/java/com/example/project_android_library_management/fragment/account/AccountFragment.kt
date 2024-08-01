package com.example.project_android_library_management.fragment.account

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.dao.AccountDao
import com.example.project_android_library_management.dao.LibrarianDao
import com.example.project_android_library_management.model.Librarian
import com.google.android.material.imageview.ShapeableImageView
import java.io.File

class AccountFragment : Fragment() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var accountDao: AccountDao
    private lateinit var librarianDao: LibrarianDao

    private lateinit var imgAvatar: ShapeableImageView
    private lateinit var tvName: TextView
    private lateinit var tvDateOfBirth: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvPhoneNumber: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvAddress: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        databaseHelper = DatabaseHelper(requireContext())
        accountDao = AccountDao(databaseHelper)
        librarianDao = LibrarianDao(databaseHelper)

        imgAvatar = view.findViewById(R.id.imgAvatar)
        tvName = view.findViewById(R.id.tvName)
        tvDateOfBirth = view.findViewById(R.id.tvDateOfBirth)
        tvGender = view.findViewById(R.id.tvGender)
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvAddress = view.findViewById(R.id.tvAddress)

        val sharedPreferences = requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val accountId = sharedPreferences.getString("ACCOUNT_ID", null)

        if (accountId != null) {
            val account = accountDao.getAccountById(accountId)
            if (account != null) {
                if (account.MaTT != null) {
                    val librarian = librarianDao.getLibrarianById(account.MaTT)
                    if (librarian != null) {
                        updateUI(view, librarian)
                    }
                } else {
                    tvName.text = account.Username
                    tvDateOfBirth.text = ""
                    tvGender.text = ""
                    tvPhoneNumber.text = ""
                    tvEmail.text = ""
                    tvAddress.text = ""
                }
            }
        } else {
            Toast.makeText(requireContext(), "Không tìm thấy thông tin tài khoản", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun updateUI(view: View, librarian: Librarian) {
        tvName.text = librarian.HoTen
        tvDateOfBirth.text = librarian.NgaySinh
        tvGender.text = librarian.GioiTinh
        tvPhoneNumber.text = librarian.DienThoai
        tvEmail.text = librarian.Email
        tvAddress.text = librarian.DiaChi

        val imagePath = librarian.HinhAnh
        if (imagePath != null) {
            val imgFile = File(imagePath)
            if (imgFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                imgAvatar.setImageBitmap(bitmap)
            }
        }
    }
}