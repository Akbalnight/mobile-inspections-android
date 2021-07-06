package ru.madbrains.inspection.ui.common.camera

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_camera.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


@SuppressLint("RestrictedApi")
class CameraFragment : BaseFragment(R.layout.fragment_camera) {
    companion object {
        private const val CAMERA_PERMISSIONS_REQUEST_CODE = 1
        private const val REQUEST_TAKE_PHOTO_FROM_GALLERY = 1000
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

    }

    private val cameraViewModel: CameraViewModel by sharedViewModel()

    private var displayId: Int = -1
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private lateinit var cameraExecutor: ExecutorService

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
        cameraExecutor.shutdown()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requestCameraPermissions()

        llPhoto.setOnClickListener {
            cameraViewModel.changeCameraState(CameraViewModel.CameraState.PHOTO)
        }

        llGallery.setOnClickListener {
            cameraViewModel.galleryClick()
        }

        llVideo.setOnClickListener {
            cameraViewModel.changeCameraState(CameraViewModel.CameraState.VIDEO)
        }

        ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        ivFlash.setOnClickListener {
            changeFlashlightState()
        }
        ivShot.setOnClickListener {
            cameraViewModel.startCapture()
        }
        ivChangeCamera.setOnClickListener {
            changeFacing()
        }
        ivStartRecord.setOnClickListener {
            cameraViewModel.startRecord()
        }
        ivStopRecord.setOnClickListener {
            ivStopRecord.isInvisible = true
            ivStartRecord.isInvisible = false
            videoCapture?.stopRecording()
        }
        cameraViewModel.startCapture.observe(viewLifecycleOwner, EventObserver { photoFile ->
            imageCapture?.let { imageCapture ->
                val metadata = ImageCapture.Metadata().apply {
                    isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
                }
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                    .setMetadata(metadata)
                    .build()
                imageCapture.takePicture(
                    outputOptions,
                    cameraExecutor,
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onError(exc: ImageCaptureException) {
                        }

                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            cameraViewModel.postFile(photoFile)
                        }
                    })
            }
        })
        cameraViewModel.startRecording.observe(viewLifecycleOwner, EventObserver { videoFile ->
            ivStartRecord.isInvisible = true
            ivStopRecord.isInvisible = false
            videoCapture?.let { videoCapture ->
                val metadata = VideoCapture.Metadata().apply {}
                val outputOptions = VideoCapture.OutputFileOptions.Builder(videoFile)
                    .setMetadata(metadata)
                    .build()
                videoCapture.startRecording(
                    outputOptions,
                    cameraExecutor,
                    object : VideoCapture.OnVideoSavedCallback {
                        override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                            cameraViewModel.postFile(videoFile)
                        }

                        override fun onError(
                            videoCaptureError: Int,
                            message: String,
                            cause: Throwable?
                        ) {
                            Timber.d("debug_dmm message: ${message}")
                        }
                    })
            }

        })
        cameraViewModel.cameraState.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                CameraViewModel.CameraState.PHOTO -> {
                    ivShot.isInvisible = false
                    ivStartRecord.isInvisible = true
                    ivStopRecord.isInvisible = true
                    llPhoto.isInvisible = true
                    llVideo.isInvisible = false
                }
                CameraViewModel.CameraState.VIDEO -> {
                    ivShot.isInvisible = true
                    ivStartRecord.isInvisible = false
                    ivStopRecord.isInvisible = true
                    llPhoto.isInvisible = false
                    llVideo.isInvisible = true
                }
            }
        })

        cameraViewModel.toGallery.observe(viewLifecycleOwner, EventObserver {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                REQUEST_TAKE_PHOTO_FROM_GALLERY
            )
        })
        cameraViewModel.popNav.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO_FROM_GALLERY && resultCode == RESULT_OK) {
            val contentResolver = activity?.contentResolver
            val uri = data?.data
            if (contentResolver != null && uri != null) {
                cameraViewModel.getDataFromGallery(uri, contentResolver)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setUpCamera()
                } else {
                    requestCameraPermissions()
                }
            }
        }
    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            cameraExecutor = Executors.newSingleThreadExecutor()
            displayId = viewFinder.display.displayId
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun requestCameraPermissions() {
        requestPermissions(
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
            CAMERA_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun bindCameraUseCases() {
        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        val rotation = viewFinder.display.rotation

        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        preview = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()

        videoCapture = VideoCapture.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()

        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageCapture,
                videoCapture
            )
            preview?.setSurfaceProvider(viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Timber.d("debug_dmm exc: $exc")
        }
    }

    private fun changeFlashlightState() {
        val value = camera?.cameraInfo?.torchState?.value == TorchState.OFF
        camera?.cameraControl?.enableTorch(value)
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun changeFacing() {
        lensFacing =
            if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
        setUpCamera()
    }

    private fun hideSystemUI() {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    private fun showSystemUI() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}