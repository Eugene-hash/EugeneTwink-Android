package ru.eugenehash.eugenetwink

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import ru.eugenehash.eugenetwink.databinding.ActivityMainBinding
import ru.eugenehash.eugenetwink.setting.SettingModel

class MainActivity : Activity() {

    private val binding get() = _binding!!
    private var _binding: ActivityMainBinding? = null

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val model = ViewModelProvider(this)[SettingModel::class.java]
        model.setSetting(intent.getParcelableExtra("setting")!!)

        binding.navHost.post {
            val navController = Navigation.findNavController(binding.navHost)
            NavigationUI.setupWithNavController(binding.navView, navController)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.camera) {
            startActivity(KIntent(this@MainActivity, CameraActivity::class))
        }; return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        bluetoothSerial.destroy()
        super.onDestroy()
        _binding = null
    }
}