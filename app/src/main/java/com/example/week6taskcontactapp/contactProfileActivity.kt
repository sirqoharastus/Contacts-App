package com.example.week6taskcontactapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class contactProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_profile)

        val intent = intent
        val name = intent.getStringExtra("name")
        val number = intent.getStringExtra("phoneNumber")
        val id = intent.getStringExtra("id")

        val nameTextView = findViewById<TextView>(R.id.contact_profile_name_textview)
        val phoneNumberTextView = findViewById<TextView>(R.id.contact_profile_phone_number)
        val callBtn = findViewById<ImageView>(R.id.call_button_imageview)
        val deleteBtn = findViewById<ImageView>(R.id.delete_button_imageview)
        val shareBtn = findViewById<ImageView>(R.id.share_imageview)


        nameTextView.text = name
        phoneNumberTextView.text = number

        callBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(number)))
            startActivity(intent)
        }

        deleteBtn.setOnClickListener {

            val myDialog = AlertDialog.Builder(this)
            myDialog.setTitle("Are you sure you want to delete this contact?")
            myDialog.setPositiveButton("YES") { dialog, which ->
                val contact = ContactsViewmodel()
                contact.deleteContact(ContactDataModelClass(id, name, number))
                Toast.makeText(this, "Contact Deleted", Toast.LENGTH_LONG).show()
            }
            myDialog.setNegativeButton("NO") { dialog, which ->
                dialog.dismiss()
            }
            myDialog.create()
            myDialog.show()

        }

        shareBtn.setOnClickListener {
            shareContact(name, number)
        }


    }

    fun shareContact(name: String?, number: String?) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(intent, "Send $name's contact to:")
        startActivity(intent)
    }
}