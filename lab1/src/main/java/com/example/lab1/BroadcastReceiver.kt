package com.example.lab1

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController

class BroadcastReceiver : Fragment() {
    private var airplaneModeReceiver: AirplaneModeReceiver? = null
    private lateinit var statusText: TextView

    inner class AirplaneModeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
                val isAirplaneModeOn = Settings.Global.getInt(
                    context?.contentResolver,
                    Settings.Global.AIRPLANE_MODE_ON, 0
                ) != 0

                val message = if (isAirplaneModeOn) "Plane mode opened" else "Plane mode closed"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                // update UI
                statusText.text = "Current status：$message"
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.broadcast_receiver, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusText = view.findViewById(R.id.statusText)
        val buttonBack = view.findViewById<Button>(R.id.buttonBack)

        buttonBack?.setOnClickListener {
            findNavController().navigate(R.id.MainPage)
        }

        // Initialize status display
        val isAirplaneModeOn = Settings.Global.getInt(
            requireContext().contentResolver,
            Settings.Global.AIRPLANE_MODE_ON, 0
        ) != 0
        statusText.text = "Current Status：${if (isAirplaneModeOn) "Plane mode opened" else "Plane mode closed"}"
    }

    override fun onResume() {
        super.onResume()
        // register broadcast receiver
        airplaneModeReceiver = AirplaneModeReceiver()
        requireContext().registerReceiver(
            airplaneModeReceiver,
            IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        )
    }

    override fun onPause() {
        super.onPause()
        // unregister b.receiver
        airplaneModeReceiver?.let {
            requireContext().unregisterReceiver(it)
        }
        airplaneModeReceiver = null
    }
}
