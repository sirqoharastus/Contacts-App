package com.example.week6taskcontactapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.lang.Exception

class ContactsViewmodel : ViewModel() {
    //declaring variables
    private val contactsDB = FirebaseDatabase.getInstance().getReference(CONTACTS_NODE)
    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?> get() = _result

    private val _contact = MutableLiveData<ContactDataModelClass>()
    val contact: LiveData<ContactDataModelClass> get() = _contact

    // function to add contacts to the database
    fun addContacts(contacts: ContactDataModelClass) {
        contacts.id = contactsDB.push().key

        contactsDB.child(contacts.id!!).setValue(contacts).addOnCompleteListener {
            if (it.isSuccessful) {
                _result.value = null
            } else {
                _result.value = it.exception
            }
        }

    }

    private val childEventListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) {

        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            val contact = snapshot.getValue(ContactDataModelClass::class.java)
            contact?.id = snapshot.key
            _contact.value = contact!!
        }

        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val contact = snapshot.getValue(ContactDataModelClass::class.java)
            contact?.id = snapshot.key
            _contact.value = contact!!
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val contact = snapshot.getValue(ContactDataModelClass::class.java)
            contact?.id = snapshot.key
            contact?.isDeleted = true
            _contact.value = contact!!
        }
    }

    fun getRealtimeUpdate() {
        contactsDB.addChildEventListener(childEventListener)
    }

    fun updateContacts(contact: ContactDataModelClass) {
        contactsDB.child(contact.id!!).setValue(contact)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _result.value = null
                } else {
                    _result.value = it.exception
                }
            }
    }

    fun deleteContact(contact: ContactDataModelClass) {
        contactsDB.child(contact.id!!).setValue(null)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _result.value = null
                } else {
                    _result.value = it.exception
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        contactsDB.removeEventListener(childEventListener)
    }
}