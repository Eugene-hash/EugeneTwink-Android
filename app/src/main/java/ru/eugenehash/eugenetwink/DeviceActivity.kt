package ru.eugenehash.eugenetwink

import android.os.Bundle
import ru.eugenehash.eugenetwink.databinding.ActivityDeviceBinding

class DeviceActivity : Activity() {

    private val binding get() = _binding!!
    private var _binding: ActivityDeviceBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}