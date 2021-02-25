package ru.madbrains.inspection.ui.main.defects.defectdetail

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_defect_detail.*
import kotlinx.android.synthetic.main.fragment_defect_list.toolbarLayout
import kotlinx.android.synthetic.main.toolbar_with_back.view.*
import kotlinx.android.synthetic.main.toolbar_with_menu.view.tvTitle
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.DefectMediaAdapter
import ru.madbrains.inspection.ui.common.camera.CameraViewModel

class DefectDetailFragment : BaseFragment(R.layout.fragment_defect_detail) {

    private val defectTypicalAdapter by lazy {
        DefectTypicalListAdapter(
                context = context,
                layoutResource = R.layout.item_defect_typical,
                textViewResourceId = R.id.tvName
        )
    }

    private val defectMediaAdapter by lazy{
        DefectMediaAdapter(
                onDefectMediaClick = {

                }
        )
    }

    private val defectDetailViewModel: DefectDetailViewModel by sharedViewModel()
    private val cameraViewModel: CameraViewModel by sharedViewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            setupEditDefect()
        } ?: run {
            setupNewDefect()
        }

        setupDefectTypical()

        setupDefectDevice()

        layoutDropDownDevice.setEndIconOnClickListener {
            openDeviceSelect()
        }
        dropDownDevice.setOnClickListener {
            openDeviceSelect()
        }

        setupMediaList()

        defectDetailViewModel.navigateToCamera.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_defectDetailFragment_to_cameraFragment)
        })

        llBottomPhotoVideo.setOnClickListener {
            defectDetailViewModel.photoVideoClick()
        }
    }

    private fun setupNewDefect() {
        toolbarLayout.apply {
            tvTitle.text = strings[R.string.fragment_defect_add_title]
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupEditDefect() {
        toolbarLayout.apply {
            tvTitle.text = strings[R.string.fragment_defect_edit_title]
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupDefectTypical() {
        defectDetailViewModel.getDefectTypicalList()

        dropDownTypeDefect.setAdapter(defectTypicalAdapter)

        defectDetailViewModel.defectTypicalList.observe(viewLifecycleOwner, Observer {
            defectTypicalAdapter.addItems(it)
        })

        dropDownTypeDefect.setOnItemClickListener { _, _, position, _ ->
            val item = defectTypicalAdapter.getItem(position)
            defectDetailViewModel.changeCurrentDefectTypical(item)
            dropDownTypeDefect.setText(item.name, false)
        }
    }

    private fun setupDefectDevice() {
        defectDetailViewModel.device.observe(viewLifecycleOwner, Observer {
            dropDownDevice.setText(it.name, false)
        })
    }

    private fun openDeviceSelect() {
        findNavController().navigate(R.id.action_defectDetailFragment_to_deviceSelectListFragment)
    }

    private fun setupMediaList() {

        rvAddDefectMedia.adapter = defectMediaAdapter

        defectDetailViewModel.mediaList.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                rvAddDefectMedia.visibility = View.VISIBLE
                tvMediaListNoData.visibility = View.GONE
            }
            else {
                tvMediaListNoData.visibility = View.VISIBLE
                rvAddDefectMedia.visibility = View.GONE
            }
            defectMediaAdapter.items = it
        })

        cameraViewModel.capturedImage.observe(viewLifecycleOwner, EventObserver {
            defectDetailViewModel.addImage(it)
        })

    }
}