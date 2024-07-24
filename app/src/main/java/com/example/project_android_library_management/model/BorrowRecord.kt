package com.example.project_android_library_management.model

data class BorrowRecord (
    val MaPM: String,
    val NgayMuon: String,
    val SoNgayMuon: Int,
    val TienCoc: Double,
    val GhiChu: String?,
    val MaDG: String,
    val MaTT: String
)