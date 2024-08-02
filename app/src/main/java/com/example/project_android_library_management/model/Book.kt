package com.example.project_android_library_management.model

import java.io.Serializable

data class Book(
    val MaSach: String,
    val ISBN: String,
    val TenSach: String,
    val TacGia: String,
    val MaNXB: String,
    val NamXB: Int?,
    val SoTrang: Int?,
    val SoLuongTon: Int,
    val GiaBan: Double,
    val MoTa: String?,
    val HinhAnh: ByteArray?,
    val MaTL: String
) : Serializable
