package com.example.project_android_library_management.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Book")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val bookID: Int,
    val bookName: String,
    val author: String
)