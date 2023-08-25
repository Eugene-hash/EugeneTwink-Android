package ru.eugenehash.eugenetwink

import android.os.Bundle
import ru.eugenehash.eugenetwink.databinding.ActivityCameraBinding

class CameraActivity : Activity() {

    private val binding get() = _binding!!
    private var _binding: ActivityCameraBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}