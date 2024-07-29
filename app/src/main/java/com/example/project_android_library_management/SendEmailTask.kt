package com.example.project_android_library_management

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast

class SendEmailTask(
    private val context: Context,
    private val to: String,
    private val subject: String,
    private val body: String
) : AsyncTask<Void, Void, Boolean>() {

    private val emailSender = EmailSender()

    override fun doInBackground(vararg params: Void?): Boolean {
        return try {
            emailSender.sendEmail(to, subject, body)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun onPostExecute(result: Boolean) {
        if (result) {
            Toast.makeText(context, "Email đã được gửi thành công", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Gửi email thất bại", Toast.LENGTH_SHORT).show()
        }
    }
}