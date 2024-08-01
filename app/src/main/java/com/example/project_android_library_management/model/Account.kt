package com.example.project_android_library_management.model

import java.io.Serializable

data class Account(
    val MaTK: String,
    val Username: String,
    val Password: String,
    val PhanQuyen: String,
    val MaTT: String?
) : Serializable