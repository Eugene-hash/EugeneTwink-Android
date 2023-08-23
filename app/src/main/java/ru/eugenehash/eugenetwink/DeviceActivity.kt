package ru.eugenehash.eugenetwink

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import ru.eugenehash.eugenetwink.bluetooth.RecyclerAdapter
import ru.eugenehash.eugenetwink.bluetooth.RecyclerAdapter.Device
import ru.eugenehash.eugenetwink.databinding.ActivityDeviceBinding

@SuppressLint("MissingPermission")
class DeviceActivity : Activity() {

    private val devices get() = binding!!.devices
    private var binding: ActivityDeviceBinding? = null
    private lateinit var adapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = ActivityDeviceBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        registerReceiver(receiver, filter)

        adapter = RecyclerAdapter(this::onItemClickListener)
        devices.layoutManager = LinearLayoutManager(this)
        devices.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.device_action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.reload) updateDeviceList()
        else if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        updateDeviceList()
        super.onStart()
    }

    private fun updateDeviceList() {
        adapter.submitList(getDeviceList())
        bluetoothAdapter.cancelDiscovery()
        bluetoothAdapter.startDiscovery()
    }

    private fun getDeviceList(): List<Device> {
        val mutableSet = mutableSetOf<Device>()
        bluetoothAdapter.bondedDevices.forEach {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                mutableSet.add(Device(it.alias!!, it.address, it.bondState))
            } else mutableSet.add(Device(it.name, it.address, it.bondState))
        }
        return mutableSet.toList()
    }

    private val receiver = object : BroadcastReceiver() {

        @Suppress("DEPRECATION")
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    val mutableSet = mutableSetOf<Device>().apply { addAll(adapter.currentList) }

                    mutableSet += if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Device(device.alias!!, device.address, device.bondState)
                    } else Device(device.name, device.address, device.bondState)

                    adapter.submitList(mutableSet.toList())
                }

                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    val bDevice: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    if (bDevice.bondState == BluetoothDevice.BOND_BONDED) return returnAddress(bDevice.address)
                    val mutableList = mutableListOf<Device>().apply { addAll(adapter.currentList) }

                    val mDevice = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Device(bDevice.alias!!, bDevice.address, bDevice.bondState)
                    } else Device(bDevice.name, bDevice.address, bDevice.bondState)

                    val index = mutableList.indexOf(mDevice)
                    if (index >= 0) mutableList[index] = mDevice
                    else mutableList += mDevice

                    adapter.submitList(mutableList)
                }
            }
        }
    }

    @SuppressLint("SwitchIntDef")
    private fun onItemClickListener(device: Device) {
        when (device.bondState) {
            Device.BOND_BONDED -> returnAddress(device.address)
            Device.BOND_NONE -> bluetoothDeviceCreateBond(device.address)
        }
    }

    private fun returnAddress(address: String) {
        setResult(RESULT_OK, Intent().putExtra("address", address)); finish()
    }

    private fun bluetoothDeviceCreateBond(address: String) {
        bluetoothAdapter.getRemoteDevice(address).createBond()
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        bluetoothAdapter.cancelDiscovery()
        super.onDestroy()
        binding = null
    }
}