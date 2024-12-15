package by.nosarahmanda.goalconnect.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.nosarahmanda.goalconnect.Model.EventModel
import by.nosarahmanda.goalconnect.R

class EventWithoutLatestAdapter(
    private val events: MutableList<EventModel>,
    private val onEdit: (EventModel) -> Unit,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<EventWithoutLatestAdapter.EventWithoutLatestViewHolder>() {

    private var latestEvent: EventModel? = null

    inner class EventWithoutLatestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val categoryTextView: TextView = view.findViewById(R.id.categoryTextView)
        // val detailsTextView: TextView = view.findViewById(R.id.detailsTextView)
        val placeTextView: TextView = view.findViewById(R.id.placeTextView)
//        val createdAtTextView: TextView = view.findViewById(R.id.createdAtTextView)
        val editButton: Button = view.findViewById(R.id.editButton)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventWithoutLatestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.widget_item_event, parent, false)
        return EventWithoutLatestViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventWithoutLatestViewHolder, position: Int) {
        val event = events[position]
//        holder.createdAtTextView.text = event.createdAt
        holder.titleTextView.text = event.titleEvent
        holder.categoryTextView.text = event.categoryEvent
        holder.placeTextView.text = event.placeEvent
//        holder.detailsTextView.text = "${event.dateEvent} \n${event.descriptionEvent}"

        holder.editButton.setOnClickListener { onEdit(event) }
        holder.deleteButton.setOnClickListener { onDelete(event.id) }
    }

    override fun getItemCount(): Int = events.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateLatestEvent(event: EventModel) {
        latestEvent = event
        notifyItemChanged(0) // Notify the first item has changed
    }
}