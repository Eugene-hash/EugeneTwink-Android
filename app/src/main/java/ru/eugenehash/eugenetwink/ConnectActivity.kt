package ru.eugenehash.eugenetwink

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import ru.eugenehash.eugenetwink.databinding.ActivityConnectBinding

class ConnectActivity : Activity() {

    companion object {

        private val PERMISSION_ARRAY: Array<String> by lazy {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
            }; else arrayOf(BLUETOOTH_SCAN, BLUETOOTH_CONNECT)
        }
    }

    private val connect get() = binding!!.connect
    private var binding: ActivityConnectBinding? = null
    private val device = registerForActivityResult(this::deviceCallback)
    private val bluetooth = registerForActivityResult(this::bluetoothCallback)
    private val permissions = registerForRequestMultiplePermissions(this::permissionsCallback)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        if (checkSelfPermissions()) checkEnableBluetooth()
        else permissions.launch(PERMISSION_ARRAY)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun permissionsCallback(results: Map<String, Boolean>) {
        if (!checkSelfPermissions()) finish()
        else checkEnableBluetooth()
    }

    private fun checkSelfPermissions() = PERMISSION_ARRAY.all {
        checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkEnableBluetooth() {
        if (bluetoothManager.adapter.isEnabled) return activityUpdateUI()
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        connect.setOnClickListener { bluetooth.launch(intent) }
        bluetooth.launch(intent)
    }

    private fun bluetoothCallback(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) activityUpdateUI()
    }

    private fun activityUpdateUI() {
        val intent = KIntent(this@ConnectActivity, DeviceActivity::class)
        connect.setOnClickListener { device.launch(intent) }
        connect.setText(R.string.button_connect_device)
        getAddressOnPreferences()
    }

    @SuppressLint("ApplySharedPref")
    private fun deviceCallback(result: ActivityResult) {
        if (result.data == null) return
        val address = result.data!!.getStringExtra("address")!!
        preferences.edit().putString("address", address).commit()
        connectDeviceForAddress(address)
    }

    private fun getAddressOnPreferences() {
        val address = preferences.getString("address", null)
        if (address != null) connectDeviceForAddress(address)
    }

    private fun connectDeviceForAddress(address: String) {
        toastTextShow("DEVICE_ADDRESS | $address")
    }

    private fun toastTextShow(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}