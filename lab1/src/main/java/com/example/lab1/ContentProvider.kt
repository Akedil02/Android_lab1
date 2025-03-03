package com.example.lab1

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import android.provider.CalendarContract
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ContentProvider : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private val adapter = CalendarEventAdapter()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getCalendarEvents()
        } else {
            Toast.makeText(context, "Calendar permission is required to read events", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.content_provider, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        val buttonGetEvents = view.findViewById<Button>(R.id.buttonGetEvents)
        val buttonBack = view.findViewById<Button>(R.id.buttonBack)

        buttonGetEvents.setOnClickListener {
            checkCalendarPermission()
        }

        buttonBack.setOnClickListener {
            findNavController().navigate(R.id.MainPage)
        }
    }

    private fun checkCalendarPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CALENDAR
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCalendarEvents()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CALENDAR) -> {
                Toast.makeText(context, "Calendar permission is required to read events", Toast.LENGTH_SHORT).show()
                requestPermissionLauncher.launch(Manifest.permission.READ_CALENDAR)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_CALENDAR)
            }
        }
    }

    private fun getCalendarEvents() {
        val events = mutableListOf<CalendarEvent>()
        val currentTime = System.currentTimeMillis()
        val endTime = currentTime + 7 * 24 * 60 * 60 * 1000L

        val projection = arrayOf(
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND
        )

        val selection = "${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTSTART} <= ?"
        val selectionArgs = arrayOf(currentTime.toString(), endTime.toString())

        try {
            val cursor = requireContext().contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                "${CalendarContract.Events.DTSTART} ASC"
            )

            cursor?.use {
                while (cursor.moveToNext()) {
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(CalendarContract.Events.TITLE))
                    val startDate = cursor.getLong(cursor.getColumnIndexOrThrow(CalendarContract.Events.DTSTART))
                    val endDate = cursor.getLong(cursor.getColumnIndexOrThrow(CalendarContract.Events.DTEND))

                    events.add(CalendarEvent(title, startDate, endDate))
                }
            }

            if (events.isEmpty()) {
                Toast.makeText(context, "Rarely event not found", Toast.LENGTH_SHORT).show()
            }
            
            adapter.updateEvents(events)
            
        } catch (e: Exception) {
            Toast.makeText(context, "Read calendar failed：${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
