package by.nosarahmanda.goalconnect.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.nosarahmanda.goalconnect.Model.EventModel
import by.nosarahmanda.goalconnect.R

class EventAdapter(
    private val events: MutableList<EventModel>,
    private val onEdit: (EventModel) -> Unit,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

        inner class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val titleTextView: TextView = view.findViewById(R.id.titleTextView)
            val detailsTextView: TextView = view.findViewById(R.id.detailsTextView)
            val editButton: Button = view.findViewById(R.id.editButton)
            val deleteButton: Button = view.findViewById(R.id.deleteButton)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.titleTextView.text = event.titleEvent
        holder.detailsTextView.text = "${event.dateEvent} \n${event.descriptionEvent}"

        holder.editButton.setOnClickListener { onEdit(event) }
        holder.deleteButton.setOnClickListener { onDelete(event.id) }
    }

    override fun getItemCount(): Int = events.size
}