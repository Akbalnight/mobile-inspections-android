package ru.madbrains.inspection.ui.common.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Rational
import android.util.Size
import android.view.View
import android.view.WindowManager
import androidx.camera.core.*
import androidx.camera.core.CameraX.LensFacing
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_camera.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment

@SuppressLint("RestrictedApi")
class CameraFragment : BaseFragment(R.layout.fragment_camera) {
    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1
    }

    private val cameraViewModel: CameraViewModel by sharedViewModel()
    private lateinit var preview: Preview
    private lateinit var videoCapture: VideoCapture
    private var lensFacing = LensFacing.BACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mark this as a retain fragment, so the lifecycle does not get restarted on config change
        retainInstance = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideSystemUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showSystemUI()
        // Turn off all camera operations when we navigate away
        CameraX.unbindAll()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requestCameraPermission()

        ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        ivFlash.setOnClickListener {
            changeFlashlightState()
        }
        ivShot.setOnClickListener {
            cameraViewModel.setImage(textureCamera.bitmap)
            findNavController().popBackStack()
        }
        ivChangeCamera.setOnClickListener {
            changeFacing()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showCamera()
                } else {
                    requestCameraPermission()
                }
            }
        }
    }

    private fun showCamera() {
        try {
            CameraX.getCameraWithLensFacing(lensFacing)
            bindCameraCases()
        } catch (e: Exception) {
            e.printStackTrace()
            // Do nothing
        }
    }

    private fun requestCameraPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    private fun bindCameraCases() {
        CameraX.unbindAll()

        val videoCaptureConfig = VideoCaptureConfig.Builder().apply {
            setTargetRotation(textureCamera.display.rotation)
            setLensFacing(lensFacing)
        }.build()

        videoCapture = VideoCapture(videoCaptureConfig)

        val metrics = DisplayMetrics().also { textureCamera.display.getRealMetrics(it) }
        val screenSize = Size(metrics.widthPixels, metrics.heightPixels)
        val screenAspectRatio = Rational(metrics.widthPixels, metrics.heightPixels)
        val previewConfig = PreviewConfig.Builder().apply {
            setLensFacing(lensFacing)
            setTargetResolution(screenSize)
            setTargetAspectRatio(screenAspectRatio)
            setTargetRotation(textureCamera.display.rotation)
        }.build()

        preview = AutoFitPreviewBuilder.build(previewConfig, textureCamera)

        CameraX.bindToLifecycle(viewLifecycleOwner, preview, videoCapture)
    }

    private fun changeFlashlightState() {
        preview.enableTorch(!preview.isTorchOn)
    }

    private fun changeFacing() {
        lensFacing = if (lensFacing == LensFacing.FRONT) LensFacing.BACK else LensFacing.FRONT
        showCamera()
    }

    private fun hideSystemUI() {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    private fun showSystemUI() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}