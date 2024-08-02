package com.example.project_android_library_management.fragment.account

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.appcompat.widget.AppCompatButton
import com.example.project_android_library_management.DatabaseHelper
import com.example.project_android_library_management.R
import com.example.project_android_library_management.dao.AccountDao
import com.example.project_android_library_management.dao.LibrarianDao
import com.example.project_android_library_management.fragment.librarian.LibrarianDetailActivity
import com.example.project_android_library_management.fragment.librarian.LibrarianDetailActivity.Companion
import com.example.project_android_library_management.fragment.librarian.LibrarianUpdateActivity
import com.example.project_android_library_management.model.Librarian
import com.google.android.material.imageview.ShapeableImageView
import java.io.File

class AccountFragment : Fragment() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var accountDao: AccountDao
    private lateinit var librarianDao: LibrarianDao

    private var librarian: Librarian? = null

    private lateinit var imgAvatar: ShapeableImageView
    private lateinit var tvName: TextView
    private lateinit var tvDateOfBirth: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvPhoneNumber: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvAddress: TextView
    private lateinit var btnChangePassword: AppCompatButton
    private lateinit var btnEdit: AppCompatButton

    companion object {
        private const val REQUEST_CODE_UPDATE_LIBRARIAN = 1
    }

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
        btnChangePassword = view.findViewById(R.id.btnChangePassword)
        btnEdit = view.findViewById(R.id.btnEdit)

        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        sharedPreferences.getString("ACCOUNT_ID", null)?.let { accountId ->
            accountDao.getAccountById(accountId)?.let { account ->
                account.MaTT?.let { maTT ->
                    librarianDao.getLibrarianById(maTT)?.let { librarian ->
                        this.librarian = librarian
                        updateUI(librarian)
                    } ?: run {
                        Toast.makeText(requireContext(), "Không tìm thấy thông tin thủ thư", Toast.LENGTH_SHORT).show()
                    }
                } ?: run {
                    tvName.text = account.Username
                    tvDateOfBirth.text = ""
                    tvGender.text = ""
                    tvPhoneNumber.text = ""
                    tvEmail.text = ""
                    tvAddress.text = ""
                }
            } ?: run {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin tài khoản", Toast.LENGTH_SHORT).show()
            }
        }

        btnChangePassword.setOnClickListener {
            val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        btnEdit.setOnClickListener {
            val intent = Intent(requireContext(), LibrarianUpdateActivity::class.java)
            intent.putExtra("LIBRARIAN_ID", librarian?.MaTT)
            startActivityForResult(intent, REQUEST_CODE_UPDATE_LIBRARIAN)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UPDATE_LIBRARIAN && resultCode == RESULT_OK) {
            data?.getStringExtra("LIBRARIAN_ID")?.let { librarianId ->
                librarianDao.getLibrarianById(librarianId)?.let { librarian ->
                    this.librarian = librarian
                    updateUI(librarian)
                }
            }
        }
    }

    private fun updateUI(librarian: Librarian) {
        tvName.text = librarian.HoTen
        tvDateOfBirth.text = librarian.NgaySinh
        tvGender.text = librarian.GioiTinh
        tvPhoneNumber.text = librarian.DienThoai
        tvEmail.text = librarian.Email
        tvAddress.text = librarian.DiaChi

        if (librarian.HinhAnh != null) {
            val bitmap = BitmapFactory.decodeByteArray(librarian.HinhAnh, 0, librarian.HinhAnh.size)
            imgAvatar.setImageBitmap(bitmap)
        } else {
            imgAvatar.setImageResource(R.drawable.avatar)
        }
    }
}