package com.example.homehealth.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.homehealth.R
import com.example.homehealth.model.Nurse

class NursesAdapter(
    private var nurses: List<Nurse>
) : RecyclerView.Adapter<NursesAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val availabilityTextView: TextView = itemView.findViewById(R.id.availabilityTextView)
        val zipTextView: TextView = itemView.findViewById(R.id.zipTextView)
        val callButton: Button = itemView.findViewById(R.id.callButton)
        val emailButton: Button = itemView.findViewById(R.id.emailButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_nurse, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nurse = nurses[position]
        val ctx = holder.itemView.context

        holder.nameTextView.text = nurse.name
        holder.availabilityTextView.text =
            if (nurse.availability.isNotBlank()) "Available: ${nurse.availability}" else "Availability not listed"
        holder.zipTextView.text = "Zip: ${nurse.zipCode}"

        holder.callButton.setOnClickListener {
            if (nurse.phone.isNotBlank()) {
                ctx.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${nurse.phone}")))
            }
        }
        holder.emailButton.setOnClickListener {
            if (nurse.email.isNotBlank()) {
                ctx.startActivity(
                    Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${nurse.email}"))
                )
            }
        }
    }

    override fun getItemCount(): Int = nurses.size

    fun updateNurses(newNurses: List<Nurse>) {
        nurses = newNurses
        notifyDataSetChanged()
    }
}
