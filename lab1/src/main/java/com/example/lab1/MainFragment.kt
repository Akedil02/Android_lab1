package com.example.lab1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.mainpage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.buttonToIntent).setOnClickListener{
            findNavController().navigate(R.id.intents_and_deeplinks)
        }

        view.findViewById<Button>(R.id.buttonToForeground).setOnClickListener{
            findNavController().navigate(R.id.forgroundservice)
        }

        view.findViewById<Button>(R.id.buttonToBroadcast).setOnClickListener{
            findNavController().navigate(R.id.bordcast_receiver)
        }

        view.findViewById<Button>(R.id.buttonToContentProvider).setOnClickListener{
            findNavController().navigate(R.id.content_provider)
        }


    }
}