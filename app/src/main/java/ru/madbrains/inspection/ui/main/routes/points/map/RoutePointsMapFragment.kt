package ru.madbrains.inspection.ui.main.routes.points.map

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.fragment_route_points_map.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.domain.model.RouteDataModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.ui.main.routes.points.RoutePointsViewModel
import ru.madbrains.inspection.ui.main.routes.techoperations.TechOperationsFragment
import ru.madbrains.inspection.ui.view.MapPoint
import java.io.File

class RoutePointsMapFragment : BaseFragment(R.layout.fragment_route_points_map) {

    private val routePointsViewModel: RoutePointsViewModel by sharedViewModel()
    private val routePointsMapViewModel: RoutePointsMapViewModel by sharedViewModel()
    private var imageScale: Float = 1f
    private var scaleStep: Float = 0.3f
    private var bitmap: Bitmap? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        routePointsViewModel.detourModel?.let {
            routePointsMapViewModel.setData(it)
        }
        routePointsMapViewModel.mapLevels.observe(viewLifecycleOwner) { list ->
            list.find { it.isActive }?.let { item ->
                routePointsMapViewModel.loadImage(item)
            }
        }
        routePointsMapViewModel.navigateToTechOperations.observe(
            viewLifecycleOwner,
            EventObserver {
                openTechOperationsFragment(it)
            }
        )
        routePointsMapViewModel.mapImage.observe(viewLifecycleOwner) { imageFile ->
            loadImage(imageFile)
        }

        btnZoomPlus.setOnClickListener {
            val value = imageScale + scaleStep
            if (value < mapIV.maximumScale) {
                mapIV.scale = value
                imageScale = value
            }
        }
        btnZoomMinus.setOnClickListener {
            val value = imageScale - scaleStep
            if (value > mapIV.minimumScale) {
                mapIV.scale = value
                imageScale = value
            }
        }
        mapIV.setOnScaleChangeListener { scaleFactor, focusX, focusY ->
            imageScale = scaleFactor
        }

        btnMapLayers.setOnClickListener {
            findNavController().navigate(R.id.action_routePointsFragment_to_mapsLevelListFragment)
        }

        routePointsMapViewModel.mapPoints.observe(viewLifecycleOwner) { list ->
            mapIV.setOnMatrixChangeListener {
                calculatePoints(it, list)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        imageScale = 1f
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        routePointsMapViewModel.mapPoints.value?.let { list->
            mapIV.displayRect?.let { rect->
                calculatePoints(rect, list)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        routePointsViewModel.clean()
    }

    private fun calculatePoints(rectF: RectF, list: List<MapPointUiModel>) {
        val image = bitmap ?: return
        val pointSize = 100
        val scaleFactor: Double = rectF.width() / image.width.toDouble()
        pointsContainer.removeAllViews()

        list.forEach { point ->
            val params = FrameLayout.LayoutParams(pointSize, pointSize)
            point.xLocation?.let { it.times(scaleFactor) + rectF.left - (pointSize / 2) }?.also {
                params.leftMargin = it.toInt()
            }
            point.yLocation?.let { it.times(scaleFactor) + rectF.top - (pointSize / 2) }?.also {
                params.topMargin = it.toInt()
            }
            val view = MapPoint(requireContext(), point.position.toString(), point.status)
            if(point.clickable) {
                view.setOnClickListener { routePointsMapViewModel.routePointClick(point) }
            }
            pointsContainer.addView(view, params)
        }
    }

    private fun loadImage(file: File) {
        Glide
            .with(requireContext())
            .asBitmap()
            .load(file)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap = resource
                    tvError.isVisible = false
                    progressView.changeVisibility(false)
                    mapIV.isVisible = true
                    pointsContainer.isVisible = true
                    mapIV.apply {
                        setImageBitmap(resource)
                    }
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    Glide.with(requireContext()).clear(mapIV)
                    tvError.isVisible = true
                    mapIV.isVisible = false
                    pointsContainer.isVisible = false
                    progressView.changeVisibility(false)
                    super.onLoadFailed(errorDrawable)
                }

                override fun onLoadStarted(placeholder: Drawable?) {
                    tvError.isVisible = false
                    mapIV.isVisible = false
                    pointsContainer.isVisible = false
                    progressView.changeVisibility(true)
                    super.onLoadStarted(placeholder)
                }
            })
    }

    private fun openTechOperationsFragment(routeData: RouteDataModel) {
        val args = bundleOf(
            TechOperationsFragment.KEY_ROUTE_DATA to routeData
        )
        findNavController().navigate(
            R.id.action_routePointsFragment_to_techOperationsFragment,
            args
        )
    }
}