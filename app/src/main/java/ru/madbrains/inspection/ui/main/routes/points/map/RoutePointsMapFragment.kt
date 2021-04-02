package ru.madbrains.inspection.ui.main.routes.points.map

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.fragment_route_points_map.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.data.network.ApiData.apiDownloadFileUrl
import ru.madbrains.domain.model.RouteMapModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.ui.main.routes.points.RoutePointsViewModel

class RoutePointsMapFragment : BaseFragment(R.layout.fragment_route_points_map) {

    private val routePointsViewModel: RoutePointsViewModel by sharedViewModel()
    private val routePointsMapViewModel: RoutePointsMapViewModel by sharedViewModel()
    private var imageScale: Float = 1f
    private var scaleStep: Float = 0.3f

    val TEST = mutableListOf<RouteMapModel>().apply {
        add(RouteMapModel(id = "test-id-1", routeId = null, fileId = null, position = null))
        add(RouteMapModel(id = "test-id-2", routeId = null, fileId = null, position = null))
        add(RouteMapModel(id = "test-id-3", routeId = null, fileId = null, position = null))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        routePointsViewModel.detourModel?.let {
//            routePointsMapViewModel.setData(it)
            routePointsMapViewModel.setTestData(TEST)
        }

        routePointsMapViewModel.mapLevels.observe(viewLifecycleOwner) { list ->
            list.find { it.isActive }?.let { item ->
                loadImage(item.id)
            }
        }

        btnZoomPlus.setOnClickListener {
            val value = imageScale + scaleStep
            if(value < mapIV.maximumScale) {
                mapIV.scale = value
                imageScale = value
            }
        }
        btnZoomMinus.setOnClickListener {
            val value = imageScale - scaleStep
            if(value > mapIV.minimumScale) {
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
    }

    private fun loadImage(id: String) {
        val url = apiDownloadFileUrl+id
        Glide
            .with(requireContext())
            .asBitmap()
            .load(url)
            .into(object: SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                    val w = resource.width
//                    val h = resource.height
                    tvError.isVisible = false
                    progressView.changeVisibility(false)
                    mapIV.setImageBitmap(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    tvError.isVisible = true
                    progressView.changeVisibility(false)
                    super.onLoadFailed(errorDrawable)
                }

                override fun onLoadStarted(placeholder: Drawable?) {
                    tvError.isVisible = false
                    progressView.changeVisibility(true)
                    super.onLoadStarted(placeholder)
                }
            })
    }
}