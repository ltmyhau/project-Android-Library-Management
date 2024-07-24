package com.example.project_android_library_management.model

data class Book(
    val MaSach: String,
    val ISBN: String,
    val TenSach: String,
    val TacGia: String,
    val NXB: String?,
    val NamXB: Int?,
    val SoTrang: Int?,
    val SoLuongTon: Int,
    val GiaBan: Double,
    val MoTa: String?,
    val HinhAnh: String?,
    val MaTL: String
)