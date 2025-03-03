package com.example.lab1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import java.io.File

class Intents_and_deeplinks : Fragment() {

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.intents_and_deeplinks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonBack = view.findViewById<Button>(R.id.buttonBack)
        val buttonSelect = view.findViewById<Button>(R.id.buttonSelectImage)
        val buttonShare = view.findViewById<Button>(R.id.buttonShareInstagram)
        buttonBack?.setOnClickListener {
            findNavController().navigate(R.id.MainPage)
        }

        buttonSelect?.setOnClickListener {
            selectImage()
        }

        // Share to Instagram
        buttonShare?.setOnClickListener {
            shareToInstagram()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            Log.d("DeepLink", "Choosen photo URI: $imageUri")
            Toast.makeText(requireContext(), "already Choose photo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareToInstagram() {
        Log.d("DeepLink", "Trying to share photo to Instagram...")
        if (imageUri == null) {
            Toast.makeText(requireContext(), "choose photo first", Toast.LENGTH_SHORT).show()
            return
        }

        val shareableUri = getSharableUri(imageUri!!)

        val intent = Intent("com.instagram.share.ADD_TO_STORY").apply {
            setDataAndType(shareableUri, "image/*")
            putExtra("interactive_asset_uri", shareableUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // 允许 Instagram 读取
        }
        try {
            startActivity(intent)
            Log.d("DeepLink", "Instagram share started successfuly！")
        } catch (e: Exception) {
            Log.e("DeepLink", "Instagram share faild：" + e.message)
            Toast.makeText(requireContext(), "cant open Instagram，check if it is installed", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getSharableUri(uri: Uri): Uri {
        val file = File(requireContext().cacheDir, "shared_image.jpg")

        requireContext().contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return FileProvider.getUriForFile(requireContext(), "com.example.lab1.fileprovider", file)
    }
}
