package ru.madbrains.inspection.ui.common.camera

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Rational
import android.util.Size
import android.view.View
import android.view.WindowManager
import androidx.camera.core.*
import androidx.camera.core.CameraX.LensFacing
import androidx.core.view.isInvisible
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_camera.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import timber.log.Timber
import java.io.*


@SuppressLint("RestrictedApi")
class CameraFragment : BaseFragment(R.layout.fragment_camera) {
    companion object {
        private const val CAMERA_PERMISSIONS_REQUEST_CODE = 1
        private const val REQUEST_TAKE_PHOTO_FROM_GALLERY = 1000
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
            cameraViewModel.setImage(textureCamera.bitmap)
        }
        ivChangeCamera.setOnClickListener {
            changeFacing()
        }
        ivStartRecord.setOnClickListener {
            cameraViewModel.startRecord("${System.currentTimeMillis()}.mp4")
        }
        ivStopRecord.setOnClickListener {
            ivStopRecord.isInvisible = true
            ivStartRecord.isInvisible = false
            videoCapture.stopRecording()
        }
        cameraViewModel.startRecording.observe(viewLifecycleOwner, EventObserver { videoFile ->
            ivStartRecord.isInvisible = true
            ivStopRecord.isInvisible = false
            videoCapture.startRecording(videoFile, object : VideoCapture.OnVideoSavedListener {
                override fun onVideoSaved(file: File?) {
                    file?.let { cameraViewModel.postFile(it) }
                }

                override fun onError(
                    useCaseError: VideoCapture.UseCaseError?,
                    message: String?,
                    cause: Throwable?
                ) {
                    Timber.tag("CameraLog").d(message)
                }
            })
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
            val dir = activity?.externalCacheDir
            val contentResolver = activity?.contentResolver
            val dataData = data?.data

            if(dir!=null && contentResolver!=null && dataData!=null){
                cameraViewModel.getDataFromGallery(dataData, dir, contentResolver)
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
                    showCamera()
                } else {
                    requestCameraPermissions()
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

    private fun requestCameraPermissions() {
        requestPermissions(
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
            CAMERA_PERMISSIONS_REQUEST_CODE
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