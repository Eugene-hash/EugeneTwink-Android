package ru.eugenehash.eugenetwink.bluetooth

import android.bluetooth.BluetoothSocket
import java.io.InputStream
import java.io.OutputStream

class BluetoothSerial private constructor(private val socket: BluetoothSocket) {

    companion object {

        val instance get() = _instance!!
        private var _instance: BluetoothSerial? = null

        fun begin(socket: BluetoothSocket) {
            _instance = BluetoothSerial(socket)
        }
    }

    private val input: InputStream = socket.inputStream
    private val output: OutputStream = socket.outputStream

    fun available() = input.available()

    fun read() = input.read()

    fun read(data: ByteArray) = input.read(data)

    fun write(vararg data: Byte) = output.write(data)

    fun destroy() = socket.close()
}