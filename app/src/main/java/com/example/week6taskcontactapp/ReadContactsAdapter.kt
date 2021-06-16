package com.example.week6taskcontactapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReadContactsAdapter(var phoneContactsModelList: ArrayList<ReadContactsModelClass>) :
    RecyclerView.Adapter<ReadContactsAdapter.ReadContactsViewHolder>() {
    inner class ReadContactsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.read_contact_name)
        val phoneNumber = view.findViewById<TextView>(R.id.read_contact_phone_number)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadContactsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.read_contacts_layout, parent, false)
        return ReadContactsViewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return phoneContactsModelList.size
    }

    override fun onBindViewHolder(holder: ReadContactsViewHolder, position: Int) {
        var contactListPosition = phoneContactsModelList[position]
        holder.name.text = contactListPosition.name
        holder.phoneNumber.text = contactListPosition.phoneNumber

    }
}