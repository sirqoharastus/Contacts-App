package com.example.week6taskcontactapp

import android.Manifest.permission.READ_CONTACTS
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ReadContactsActivity : AppCompatActivity() {
    // declaring variables
    lateinit var recyclerView: RecyclerView
    val contactsArrayList = arrayListOf<ReadContactsModelClass>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_contacts)
        //declaring recyclerview and its adapter
        recyclerView = findViewById(R.id.read_contacts_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        checkPermission()

    }

    // function to check permission
    @RequiresApi(Build.VERSION_CODES.O)
    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        )
            ActivityCompat.requestPermissions(
                this,
                arrayOf(READ_CONTACTS),
                100
            ) else getContactList()
    }

    //function to get contact list
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getContactList() {
        val uri = ContactsContract.Contacts.CONTENT_URI
        val sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        val cursor = contentResolver.query(uri, null, null, null, sort)
        if (cursor?.count!! > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                val selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?"

                val phoneCursor =
                    contentResolver.query(uriPhone, null, selection, arrayOf(id), null)

                if (phoneCursor!!.moveToNext()) {
                    val number =
                        phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val contactModel = ReadContactsModelClass(name, number)
                    contactsArrayList.add(contactModel)

                    phoneCursor.close()

                }

            }

            cursor.close()
        }
        Log.d("CHECKERS", "$contactsArrayList")
        recyclerView.adapter = ReadContactsAdapter(contactsArrayList)
    }

}