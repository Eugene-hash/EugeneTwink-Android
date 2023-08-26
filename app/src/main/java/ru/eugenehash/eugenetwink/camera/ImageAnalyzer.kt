package ru.eugenehash.eugenetwink.camera

import android.os.Handler
import android.os.Looper
import androidx.camera.core.ImageAnalysis.Analyzer
import androidx.camera.core.ImageProxy
import ru.eugenehash.eugenetwink.bluetooth.BluetoothSerial
import ru.eugenehash.eugenetwink.command.Camera

class ImageAnalyzer(private val callback: Callback) : Analyzer {

    private var isInit = true
    private val handler = Handler(Looper.getMainLooper())
    private val bluetoothSerial = BluetoothSerial.instance
    private val matrix = arrayOf(
        arrayOf(0, 0), arrayOf(1, 0), arrayOf(1, 0), arrayOf(3, 0),
        arrayOf(0, 1), arrayOf(1, 1), arrayOf(1, 1), arrayOf(3, 1),
        arrayOf(0, 2), arrayOf(1, 2), arrayOf(1, 2), arrayOf(3, 2),
        arrayOf(0, 3), arrayOf(1, 3), arrayOf(1, 3), arrayOf(3, 3),
    )

    override fun analyze(image: ImageProxy) {
        if (!isInit) {
            val planeProxy = image.planes[0]
            val buffer = planeProxy.buffer

            var maxPixel = Pixel(0, 0, 0u)
            val ySize = planeProxy.rowStride
            val xSize = buffer.capacity() / ySize

            for (x in 0..<xSize step 4) {
                for (y in 0..<ySize step 4) {
                    var bright: UInt = 0u

                    matrix.forEach {
                        val yPos = y + it[0]
                        val xPos = (x + it[1]) * ySize
                        bright += buffer.get(xPos + yPos).toUInt()
                    }

                    if (maxPixel.bright < bright) {
                        maxPixel = Pixel(x shr 2, y shr 2, bright)
                    }
                }
            }

            bluetoothSerial.write((maxPixel.x shr 8).toByte(), maxPixel.x.toByte())
            bluetoothSerial.write((maxPixel.y shr 8).toByte(), maxPixel.y.toByte())
        } else {
            val size = bluetoothSerial.available()
            bluetoothSerial.read(ByteArray(size))
            bluetoothSerial.write(Camera.SCAN_START)
            isInit = false
        }

        @Suppress("ControlFlowWithEmptyBody") while (bluetoothSerial.available() == 0);
        if (bluetoothSerial.read() == Camera.SCAN_STOP.toInt()) {
            handler.post(callback::onPostExecuteMain)
            callback.onPostExecuteBackground()
        }

        image.close()
    }

    data class Pixel(var x: Int, var y: Int, var bright: UInt)

    interface Callback {

        fun onPostExecuteMain()

        fun onPostExecuteBackground()
    }
}