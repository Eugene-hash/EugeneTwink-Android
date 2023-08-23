package ru.eugenehash.eugenetwink

import android.os.Bundle
import ru.eugenehash.eugenetwink.databinding.ActivityMainBinding

class MainActivity : Activity() {

    private val binding get() = _binding!!
    private var _binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        bluetoothSerial.destroy()
        super.onDestroy()
        _binding = null
    }
}