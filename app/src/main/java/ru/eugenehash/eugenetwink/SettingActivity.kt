package ru.eugenehash.eugenetwink

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import ru.eugenehash.eugenetwink.bluetooth.BluetoothSerial
import ru.eugenehash.eugenetwink.command.Main
import ru.eugenehash.eugenetwink.setting.Setting

class SettingActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        GetSettingThread().start()
    }

    private inner class GetSettingThread : Thread() {

        private var delay = true
        private var timeout = false
        private var setting = Setting()
        private val handler = Handler(Looper.getMainLooper())
        private val bluetoothSerial get() = BluetoothSerial.instance

        @Suppress("ControlFlowWithEmptyBody")
        override fun run() {
            val size = bluetoothSerial.available()
            bluetoothSerial.read(ByteArray(size))

            handler.postDelayed(this::delay, 1_000)
            handler.postDelayed(this::timeout, 10_000)

            bluetoothSerial.write(Main.SETTING)
            while (delay || (bluetoothSerial.available() < 4 && !timeout));

            if (bluetoothSerial.available() >= 4) {
                val data = ByteArray(4)
                bluetoothSerial.read(data)
                setting.bright = data[1].toInt()
                setting.power = data[0] == 1.toByte()
                setting.random = data[2] == 1.toByte()
                setting.autoplay = data[3] == 1.toByte()
            }

            handler.post(this::onPostExecute)
        }

        private fun delay() {
            delay = false
        }

        private fun timeout() {
            timeout = true
        }

        @Suppress("DEPRECATION")
        private fun onPostExecute() {
            val intent = KIntent(this@SettingActivity, MainActivity::class)
            startActivity(intent.putExtra("setting", setting))
            overridePendingTransition(0, 0); finish()
        }
    }
}