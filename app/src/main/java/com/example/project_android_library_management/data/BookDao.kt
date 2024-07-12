package com.example.project_android_library_management.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.project_android_library_management.model.Book

@Dao
interface BookDao {
    @Insert
    fun insertBook(book: Book)

    @Query("SELECT * FROM Book")
    fun getAllBooks(): List<Book>

}