package com.example.lab1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class CalendarEventAdapter : RecyclerView.Adapter<CalendarEventAdapter.EventViewHolder>() {
    private val events = mutableListOf<CalendarEvent>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.eventTitle)
        val timeText: TextView = view.findViewById(R.id.eventTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.titleText.text = event.title
        holder.timeText.text = "From ${dateFormat.format(Date(event.startTime))}\nTo ${dateFormat.format(Date(event.endTime))}"
    }

    override fun getItemCount() = events.size

    fun updateEvents(newEvents: List<CalendarEvent>) {
        events.clear()
        events.addAll(newEvents)
        notifyDataSetChanged()
    }
}