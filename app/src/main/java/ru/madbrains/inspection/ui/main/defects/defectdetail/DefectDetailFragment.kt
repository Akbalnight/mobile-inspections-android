package ru.madbrains.inspection.ui.main.defects.defectdetail

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_defect_detail.*
import kotlinx.android.synthetic.main.fragment_defect_detail.progressView
import kotlinx.android.synthetic.main.fragment_defect_list.toolbarLayout
import kotlinx.android.synthetic.main.toolbar_with_back.view.*
import kotlinx.android.synthetic.main.toolbar_with_menu.view.tvTitle
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.domain.model.DefectModel
import ru.madbrains.domain.model.EquipmentModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.colors
import ru.madbrains.inspection.extensions.formattedStrings
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.DefectMediaAdapter
import ru.madbrains.inspection.ui.common.camera.CameraViewModel
import ru.madbrains.inspection.ui.delegates.MediaDefectUiModel
import ru.madbrains.inspection.ui.main.defects.defectdetail.equipmentselectlist.EquipmentSelectListViewModel

class DefectDetailFragment : BaseFragment(R.layout.fragment_defect_detail) {

    companion object {
        const val KEY_EQUIPMENT_LIST = "equipment_list_defect_detail_fragment"
        const val KEY_DETAIL_DEFECT = "defect_model_defect_detail_fragment"
        const val KEY_DETOUR_ID = "detour_id_defect_detail_fragment"
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

    private val defectDetailViewModel: DefectDetailViewModel by viewModel()
    private val cameraViewModel: CameraViewModel by sharedViewModel()
    private val equipmentSelectViewModel: EquipmentSelectListViewModel by sharedViewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        defectDetailViewModel.setEquipments(this.arguments?.getSerializable(DefectDetailFragment.KEY_EQUIPMENT_LIST) as? List<EquipmentModel>)
        defectDetailViewModel.setDetourId(this.arguments?.getString(DefectDetailFragment.KEY_DETOUR_ID))
        defectDetailViewModel.setDefect(this.arguments?.getSerializable(DefectDetailFragment.KEY_DETAIL_DEFECT) as? DefectModel)
        arguments?.let {
            setupEditDefect()
        } ?: run {
            setupNewDefect() //todo
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

        setupNavigation()


        defectDetailViewModel.progressVisibility.observe(viewLifecycleOwner, Observer {
            progressView.changeVisibility(it)
        })

        defectDetailViewModel.descriptionObserver.observe(viewLifecycleOwner, Observer {
            etDescription.editText?.setText(it)
        })


    }

    private fun setupNewDefect() {
        toolbarLayout.apply {
            tvTitle.text = strings[R.string.fragment_defect_add_title]
            btnLeading.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupEditDefect() {
        toolbarLayout.apply {
            tvTitle.text = strings[R.string.fragment_defect_edit_title]
            btnLeading.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupNavigation() {
        defectDetailViewModel.navigateToCamera.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_defectDetailFragment_to_cameraFragment)
        })

        defectDetailViewModel.popNavigation.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })
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

        // клик по полю ввода
        dropDownDevice.setOnClickListener { toDeviceSelect() }

        layoutDropDownDevice.setEndIconOnClickListener { toDeviceSelect() }

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

        defectDetailViewModel.defectTypicalList.observe(viewLifecycleOwner, Observer {
            dropDownTypeDefect.setAdapter(DefectTypicalListAdapter(
                    context = context,
                    layoutResource = R.layout.item_defect_typical,
                    textViewResourceId = R.id.tvName,
                    values = it))
        })

        dropDownTypeDefect.setOnItemClickListener { _, _, position, _ ->
            val item = dropDownTypeDefect.adapter.getItem(position) as DefectTypicalUiModel
            defectDetailViewModel.changeCurrentDefectTypical(item)
            dropDownTypeDefect.setText(item.name, false)
        }

        defectDetailViewModel.disableTypicalDefectField.observe(viewLifecycleOwner, Observer {
            layoutDropDownTypeDefect.isEnabled = false
            layoutDropDownTypeDefect.endIconMode = TextInputLayout.END_ICON_NONE
            dropDownTypeDefect.setTextColor(colors[R.color.black50])
            dropDownTypeDefect.setText(it.peekContent())
        })
    }

    private fun setupDefectDevice() {
        defectDetailViewModel.deviceName.observe(viewLifecycleOwner, Observer { equipment ->
            equipment?.let {
                dropDownDevice.setText(it, false)
            }
        })

        defectDetailViewModel.disableEquipmentField.observe(viewLifecycleOwner, Observer {
            layoutDropDownDevice.isEnabled = false
            layoutDropDownDevice.endIconMode = TextInputLayout.END_ICON_NONE
            dropDownDevice.setTextColor(colors[R.color.black50])
        })

        equipmentSelectViewModel.checkedDevice.observe(
                viewLifecycleOwner,
                EventObserver {
                    defectDetailViewModel.changeCurrentDefectDevice(it)
                })
    }

    private fun toDeviceSelect() {
        equipmentSelectViewModel.setCurrentDevice(defectDetailViewModel.getCurrentDevice())
        equipmentSelectViewModel.setEquipments(defectDetailViewModel.equipmentModelList)
        findNavController().navigate(R.id.action_defectDetailFragment_to_deviceSelectListFragment)
    }

    private fun setupDialogs() {
        defectDetailViewModel.showDialogBlankFields.observe(viewLifecycleOwner, EventObserver {
            showDialogEmptyFields()
        })

        defectDetailViewModel.showDialogBlankRequiredFields.observe(viewLifecycleOwner, EventObserver {
            showDialogEmptyRequiredFields()
        })
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
                            defectDetailViewModel.saveDefect()
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