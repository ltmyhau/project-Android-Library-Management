package com.example.project_android_library_management

import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailSender {
    fun sendEmail(recipient: String, subject: String, body: String) {
        val props = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication("2121001000@sv.ufm.edu.vn", "Kop15348")
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress("2121001000@sv.ufm.edu.vn"))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient))
                setSubject(subject)
                setText(body)
            }

            Transport.send(message)
            println("Email sent successfully")

        } catch (e: MessagingException) {
            e.printStackTrace()
            println("Failed to send email: ${e.message}")
        }
    }
}
