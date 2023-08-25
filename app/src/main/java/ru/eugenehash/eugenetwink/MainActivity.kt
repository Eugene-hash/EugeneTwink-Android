package ru.eugenehash.eugenetwink

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ru.eugenehash.eugenetwink.databinding.ActivityMainBinding
import ru.eugenehash.eugenetwink.setting.SettingModel

class MainActivity : Activity() {

    private val binding get() = _binding!!
    private var _binding: ActivityMainBinding? = null
    private val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration(setOf(R.id.navigation_setting, R.id.navigation_modes))
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val model = ViewModelProvider(this)[SettingModel::class.java]
        model.setSetting(intent.getParcelableExtra("setting")!!)

        binding.navHost.post {
            val navController = Navigation.findNavController(binding.navHost)
            setupActionBarWithNavController(navController, appBarConfiguration)
            binding.navView.setupWithNavController(navController)
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