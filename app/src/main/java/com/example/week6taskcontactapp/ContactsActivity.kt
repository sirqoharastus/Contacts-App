package com.example.week6taskcontactapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.week6taskcontactapp.R.layout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase


class ContactsActivity : AppCompatActivity(), OnContactItemClickedListener {
    // declaring variables
    lateinit var recyclerView: RecyclerView
    lateinit var contactsList: ArrayList<ContactDataModelClass>
    lateinit var fob: FloatingActionButton
    lateinit var contactsAdapter: ContactAdapter
    lateinit var viewmodel: ContactsViewmodel
    lateinit var viewmodel2: ContactsViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
        val viewmodel = ContactsViewmodel()
        // declaring viewmodel observer
        viewmodel.result.observe(this, Observer {
            val message = if (it == null) {
                getString(R.string.added_contact)
            } else {
                getString(R.string.error, it.message)
            }

            Toast.makeText(this, message, Toast.LENGTH_LONG).show()

        })
        // declaring bottom navigation view and setting it on click listener
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNavigationView.setOnNavigationItemReselectedListener {
            when (it.itemId) {
                R.id.contacts_menu -> startActivity(Intent(this, ReadContactsActivity::class.java))
                R.id.recents_menu -> startActivity(Intent(this, ContactsActivity::class.java))
            }
        }

        // declaring variables
        fob = findViewById(R.id.fob)
        recyclerView = findViewById(R.id.contacts_recyclerview)
        contactsList = ArrayList()
        contactsAdapter = ContactAdapter(this, contactsList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = contactsAdapter
        this.viewmodel = ContactsViewmodel()

        viewmodel2 = ContactsViewmodel()
        //setting viewmodel observer
        viewmodel2.contact.observe(this, Observer {
            contactsAdapter.addContact(it)
        })

        // setting floating action button on click listener
        fob.setOnClickListener {
            addContact()
        }

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")

        viewmodel2.getRealtimeUpdate()
        viewmodel.getRealtimeUpdate()
        val itemTouchHelper = ItemTouchHelper(simpleCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    // function to add cotacts
    private fun addContact() {
        val inflater =
            LayoutInflater.from(applicationContext).inflate(layout.add_contact, null, false)

        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(inflater)
        addDialog.setPositiveButton("ADD") {

                dialog, _ ->
            val contactName = inflater.findViewById<EditText>(R.id.new_contact_name_edittext)
            val contactPhoneNumber =
                inflater.findViewById<EditText>(R.id.new_contact_phone_number_edittext)

            val name = contactName?.text.toString().trim()
            val phoneNumber = contactPhoneNumber?.text.toString().trim()
            val contact = ContactDataModelClass()
            contact.contactName = name
            contact.contactPhoneNumber = phoneNumber
            viewmodel = ContactsViewmodel()
            viewmodel.addContacts(contact)
//            contactsList.add(ContactData("$name", "$phoneNumber"))
            contactsAdapter.notifyDataSetChanged()
            Toast.makeText(this, "CONTACT ADDED", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }


        addDialog.setNegativeButton("CANCEL") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(this, "CANCELLED", Toast.LENGTH_LONG).show()
        }
        addDialog.create()
        addDialog.show()
    }

    // function to update contact
    private fun updateContact(contact: ContactDataModelClass) {
        val inflater =
            LayoutInflater.from(applicationContext).inflate(layout.update_contact, null, false)
        val name = inflater.findViewById<EditText>(R.id.update_contact_name_edittext)
        val phoneNumber = inflater.findViewById<EditText>(R.id.update_contact_phone_number_edittext)

        name.setText(contact.contactName)
        phoneNumber.setText(contact.contactPhoneNumber)

        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(inflater)
        addDialog.setPositiveButton("Update") { dialog, which ->
            val newName = name.text.toString().trim()
            val newPhoneNumber = phoneNumber.text.toString().trim()
            //val contact = ContactData()
            contact.contactName = newName
            contact.contactPhoneNumber = newPhoneNumber
            viewmodel.updateContacts(contact)
            Toast.makeText(this, "Contact Updated", Toast.LENGTH_LONG).show()
        }
        addDialog.create()
        addDialog.show()

    }

    private var simpleCallBack =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            // function to declare actions on contact swipe
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var position = viewHolder.adapterPosition
                var currentContact = contactsAdapter.contactList[position]

                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        updateContact(currentContact)
                    }

                    ItemTouchHelper.LEFT -> {
                        val alertdialog = AlertDialog.Builder(baseContext).also {
                            it.setTitle("Are you sure you want to delete this contact?")
                                .setPositiveButton("YES") { dialog, which ->
                                    viewmodel.deleteContact(currentContact)
                                    recyclerView.adapter?.notifyItemRemoved(position)
                                    Toast.makeText(
                                        baseContext,
                                        "Contact Deleted",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                .setNegativeButton("NO") { dialog, which ->
                                    dialog.dismiss()
                                    Toast.makeText(baseContext, "Cancelled", Toast.LENGTH_LONG)
                                        .show()

                                }
                        }
                        alertdialog.create()
                        alertdialog.show()
                    }
                }
                recyclerView.adapter?.notifyDataSetChanged()
            }

        }

    override fun onContactItemClicked(position: Int) {
        var intent = Intent(this, contactProfileActivity::class.java)
        intent.putExtra("name", contactsList[position].contactName)
        intent.putExtra("phoneNumber", contactsList[position].contactPhoneNumber)
        intent.putExtra("id", contactsList[position].id)
        startActivity(intent)
    }
}