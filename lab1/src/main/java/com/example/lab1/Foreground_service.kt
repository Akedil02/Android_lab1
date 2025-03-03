package com.example.lab1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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

class Foreground_service : Fragment() {
    private lateinit var serviceIntent: Intent

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startMusicService("PLAY")
        } else {
            Toast.makeText(context, "Notification permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.foregroundservice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        serviceIntent = Intent(requireContext(), MusicService::class.java)

        val buttonPlay = view.findViewById<Button>(R.id.buttonPlay)
        val buttonPause = view.findViewById<Button>(R.id.buttonPause)
        val buttonStop = view.findViewById<Button>(R.id.buttonStop)
        val buttonBack = view.findViewById<Button>(R.id.buttonBack)

        buttonPlay.setOnClickListener {
            checkNotificationPermission("PLAY")
        }

        buttonPause.setOnClickListener {
            checkNotificationPermission("PAUSE")
        }

        buttonStop.setOnClickListener {
            checkNotificationPermission("STOP")
        }

        buttonBack.setOnClickListener {
            findNavController().navigate(R.id.MainPage)
        }
    }

    private fun checkNotificationPermission(action: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startMusicService(action)
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Toast.makeText(context, "Notification permission is required", Toast.LENGTH_SHORT).show()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            startMusicService(action)
        }
    }

    private fun startMusicService(action: String) {
        serviceIntent.action = action
        requireContext().startService(serviceIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().stopService(serviceIntent)
    }
}
