package com.example.week6taskcontactapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    val c: Context,
    val contactList: ArrayList<ContactDataModelClass>,
    val onContactItemClicked: OnContactItemClickedListener
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {
    // view holder holds the views
    inner class ContactViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val phoneNumber: TextView
        val info: ImageView

        init {
            name = view.findViewById<TextView>(R.id.read_contact_name)
            phoneNumber = view.findViewById<TextView>(R.id.read_contact_phone_number)
            info = view.findViewById<ImageView>(R.id.info_icon)

        }
    }

    // inflates the layout when the view is created
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val inflater =
            LayoutInflater.from(parent.context).inflate(R.layout.contact_layout, parent, false)
        return ContactViewHolder(inflater)

    }

    //gets the size of the contactList arrayList
    override fun getItemCount(): Int {
        return contactList.size
    }

    //Binds the views to the object
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contactList = contactList[position]
        holder.name.text = contactList.contactName
        holder.phoneNumber.text = contactList.contactPhoneNumber

        holder.view.setOnClickListener {
            onContactItemClicked.onContactItemClicked(position)
        }

    }

    // adds new contacts to the contactList
    fun addContact(contact: ContactDataModelClass) {
        if (!contactList.contains(contact)) {
            contactList.add(contact)
        } else {
            val index = contactList.indexOf(contact)
            if (contact.isDeleted) {
                contactList.removeAt(index)
            } else {
                contactList[index] = contact
            }
        }
        notifyDataSetChanged()
    }
}