package ru.madbrains.inspection.ui.main.defects.defectdetail

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
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
import ru.madbrains.inspection.extensions.formattedStrings
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.DefectMediaAdapter
import ru.madbrains.inspection.ui.common.camera.CameraViewModel
import ru.madbrains.inspection.ui.delegates.MediaDefectUiModel

class DefectDetailFragment : BaseFragment(R.layout.fragment_defect_detail) {

    private val defectTypicalAdapter by lazy {
        DefectTypicalListAdapter(
                context = context,
                layoutResource = R.layout.item_defect_typical,
                textViewResourceId = R.id.tvName
        )
    }

    private val defectMediaAdapter by lazy {
        DefectMediaAdapter(
                onMediaImageClick = {
                    // todo show image preview
                },
                onMediaDeleteClick = {
                    showDialogDeleteMedia(it)
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

        // настройка поля выбора типа дефекта
        setupDefectTypical()

        // настройка поля выбора девайса
        setupDefectDevice()

        // настройка слушателей кликов по элементу
        setupClickListeners()

        // настройка списка медиа
        setupMediaList()

        // настройка диалоговых окон
        setupDialogs()

        defectDetailViewModel.navigateToCamera.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_defectDetailFragment_to_cameraFragment)
        })

        defectDetailViewModel.popNavigation.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })
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

    private fun setupClickListeners() {

        // кнопка фото/видео нижнее меню
        llBottomPhotoVideo.setOnClickListener {
            defectDetailViewModel.photoVideoClick()
        }

        // кнопка сканировать нижнее меню
        llBottomScan.setOnClickListener {
            // todo scan
        }

        // клик по иконке выбора списка
        layoutDropDownDevice.setOnClickListener {
            openDeviceSelect()
        }

        // клик по полю ввода
        dropDownDevice.setOnClickListener {
            openDeviceSelect()
        }

        // клик по кнопке сохранения дефекта
        fabDefectSave.setOnClickListener {
            defectDetailViewModel.checkAndSave()
        }

        etAddDefectDescription.doOnTextChanged { text, _, _, _ ->
            defectDetailViewModel.addDescription(text)
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

    private fun setupDialogs() {
        defectDetailViewModel.showDialogBlankFields.observe(viewLifecycleOwner, EventObserver {
            showDialogEmptyFields()
        })

        defectDetailViewModel.showDialogBlankRequiredFields.observe(viewLifecycleOwner, EventObserver {
            showDialogEmptyRequiredFields()
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
                tvMediaList.text = formattedStrings[R.string.fragment_add_defect_media_list_title].invoke(values = *arrayOf(it.size))
            } else {
                tvMediaListNoData.visibility = View.VISIBLE
                rvAddDefectMedia.visibility = View.GONE
                tvMediaList.text = formattedStrings[R.string.fragment_add_defect_media_list_title].invoke(values = *arrayOf("0"))
            }
            defectMediaAdapter.items = it
        })

        cameraViewModel.capturedImage.observe(viewLifecycleOwner, EventObserver {
            defectDetailViewModel.addImage(it)
        })

    }

    private fun showDialogDeleteMedia(item: MediaDefectUiModel) {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setMessage(strings[R.string.fragment_add_defect_dialog_delete_subtitle])
                setPositiveButton(strings[R.string.fragment_add_defect_dialog_btn_delete],
                        DialogInterface.OnClickListener { _, _ ->
                            defectDetailViewModel.deleteMedia(item)
                        })
                setNegativeButton(strings[R.string.fragment_add_defect_dialog_btn_cancel],
                        DialogInterface.OnClickListener { _, _ ->
                        })
            }
            builder.create()
        }
        alertDialog?.show()
    }

    private fun showDialogEmptyFields() {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle(strings[R.string.fragment_add_defect_dialog_empty_fields_title])
                setMessage(strings[R.string.fragment_add_defect_dialog_empty_fields_subtitle])
                setPositiveButton(strings[R.string.fragment_add_defect_dialog_btn_save],
                        DialogInterface.OnClickListener { _, _ ->
                            defectDetailViewModel.checkAndSave()
                        })
                setNegativeButton(strings[R.string.fragment_add_defect_dialog_btn_cancel],
                        DialogInterface.OnClickListener { _, _ ->
                        })
            }
            builder.create()
        }
        alertDialog?.show()
    }

    private fun showDialogEmptyRequiredFields() {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setMessage(strings[R.string.fragment_add_defect_dialog_empty_required_fields_subtitle])
                setPositiveButton(strings[R.string.fragment_add_defect_dialog_btn_ok],
                        DialogInterface.OnClickListener { _, _ ->

                        })
            }
            builder.create()
        }
        alertDialog?.show()
    }

}