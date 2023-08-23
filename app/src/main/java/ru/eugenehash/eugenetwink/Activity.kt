package ru.eugenehash.eugenetwink

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import ru.eugenehash.eugenetwink.bluetooth.BluetoothSerial
import kotlin.reflect.KClass

open class Activity : AppCompatActivity() {

    val bluetoothSerial: BluetoothSerial
        get() = BluetoothSerial.instance

    val preferences: SharedPreferences
        get() = getPreferences(MODE_PRIVATE)

    val bluetoothAdapter: BluetoothAdapter
        get() = getSystemService(BluetoothManager::class.java).adapter

    fun registerForRequestPermission(callback: ActivityResultCallback<Boolean>) =
        registerForActivityResult(RequestPermission(), callback)

    fun registerForActivityResult(callback: ActivityResultCallback<ActivityResult>) =
        registerForActivityResult(StartActivityForResult(), callback)

    fun registerForRequestMultiplePermissions(callback: ActivityResultCallback<Map<String, Boolean>>) =
        registerForActivityResult(RequestMultiplePermissions(), callback)

    class KIntent<T : Activity>(context: Context, activity: KClass<T>) : Intent(context, activity.java)
}