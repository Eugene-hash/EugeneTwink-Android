package ru.eugenehash.eugenetwink

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat.getMainExecutor
import com.google.common.util.concurrent.ListenableFuture
import ru.eugenehash.eugenetwink.camera.ImageAnalyzer
import ru.eugenehash.eugenetwink.command.Camera
import ru.eugenehash.eugenetwink.databinding.ActivityCameraBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : Activity() {

    private val binding get() = _binding!!
    private var _binding: ActivityCameraBinding? = null
    private lateinit var analyzeExecutor: ExecutorService
    private var lastBackPresedTime = System.currentTimeMillis()
    private val permission = registerForRequestPermission(this::permissionsCallback)
    private lateinit var processCameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private val imageAnalysis by lazy {
        ImageAnalysis.Builder().setResolutionSelector(getResolutionSelector())
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bluetoothSerial.write(Camera.OPEN)

        binding.back.setOnClickListener { finish() }
        analyzeExecutor = Executors.newSingleThreadExecutor()
        binding.scan.setOnClickListener(this::onClickScanListener)

        if (!checkSelfPermission()) {
            permission.launch(CAMERA)
        }; else cameraGetInstance()
    }

    private fun onClickScanListener(view: View) {
        val analyzer = ImageAnalyzer(AnalyzerCallback())
        imageAnalysis.setAnalyzer(analyzeExecutor, analyzer)
        view.isEnabled = false
    }

    @Suppress("UNUSED_PARAMETER")
    private fun permissionsCallback(ignoredIsGranted: Boolean) {
        if (checkSelfPermission()) cameraGetInstance() else finish()
    }

    private fun checkSelfPermission() = checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED

    private fun cameraGetInstance() {
        processCameraProviderFuture = ProcessCameraProvider.getInstance(this)
        processCameraProviderFuture.addListener(this::cameraListener, getMainExecutor(this))
    }

    private fun cameraListener() {
        val processCameraProvider = processCameraProviderFuture.get()
        processCameraProvider.bindToLifecycle(this, getSelector(), getPreview(), imageAnalysis)
    }

    @Suppress("SameReturnValue")
    private fun getSelector() = CameraSelector.DEFAULT_BACK_CAMERA

    private fun getPreview() = Preview.Builder().build().also {
        it.setSurfaceProvider(binding.preview.surfaceProvider)
    }

    private fun getResolutionSelector() =
        ResolutionSelector.Builder().setResolutionStrategy(getResolutionStrategy())
            .setAspectRatioStrategy(getAspectRatioStrategy()).build()

    private fun getAspectRatioStrategy() =
        AspectRatioStrategy(AspectRatio.RATIO_16_9, AspectRatioStrategy.FALLBACK_RULE_NONE)

    private fun getResolutionStrategy() =
        ResolutionStrategy(Size(1280, 720), ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER)

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val backPressedTime = System.currentTimeMillis()
        if (lastBackPresedTime + 1000 >= backPressedTime) {
            return super.onBackPressed()
        }

        Toast.makeText(this, R.string.toast_back_pressed, Toast.LENGTH_SHORT).show()
        lastBackPresedTime = backPressedTime
    }

    override fun onDestroy() {
        bluetoothSerial.write(Camera.CLOSE)
        imageAnalysis.clearAnalyzer()
        analyzeExecutor.shutdown()
        super.onDestroy()
        _binding = null
    }

    private inner class AnalyzerCallback : ImageAnalyzer.Callback {

        override fun onPostExecuteMain() {
            binding.scan.isEnabled = true
        }

        override fun onPostExecuteBackground() {
            imageAnalysis.clearAnalyzer()
            analyzeExecutor.shutdown()
        }

    }
}