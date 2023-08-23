package ru.eugenehash.eugenetwink.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class ConnectThread(device: BluetoothDevice, private val callback: Callback) : Thread() {

    private val handler = Handler(Looper.getMainLooper())
    private val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val socket: BluetoothSocket? = device.createRfcommSocketToServiceRecord(uuid)

    override fun start() {
        callback.onPreExecute()
        super.start()
    }

    override fun run() {
        try {
            socket!!.connect()
            BluetoothSerial.begin(socket)
            handler.post(callback::onPostExecuteSuccess)
        } catch (e: IOException) {
            Log.w("ConnectThread", e)

            try {
                socket!!.close()
            } catch (c: IOException) {
                Log.w("ConnectThread", c)
            }

            handler.post(callback::onPostExecuteError)
        } catch (n: NullPointerException) {
            handler.post(callback::onPostExecuteError)
            Log.w("ConnectThread", n)
        }
    }

    interface Callback {

        fun onPreExecute()

        fun onPostExecuteSuccess()

        fun onPostExecuteError()
    }
}