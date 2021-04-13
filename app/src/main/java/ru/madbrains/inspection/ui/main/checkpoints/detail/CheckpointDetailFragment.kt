package ru.madbrains.inspection.ui.main.checkpoints.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_checkpoint_detail.*
import kotlinx.android.synthetic.main.toolbar_with_back.view.*
import kotlinx.android.synthetic.main.toolbar_with_menu.view.tvTitle
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.madbrains.domain.model.CheckpointModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.formattedStrings
import ru.madbrains.inspection.extensions.strings
import ru.madbrains.inspection.ui.adapters.MediaAdapter
import ru.madbrains.inspection.ui.common.camera.CameraViewModel
import ru.madbrains.inspection.ui.delegates.MediaUiModel
import ru.madbrains.inspection.ui.main.MainViewModel
import ru.madbrains.inspection.ui.main.checkpoints.list.CheckpointListViewModel

class CheckpointDetailFragment : BaseFragment(R.layout.fragment_checkpoint_detail) {

    companion object {
        const val KEY_CHECKPOINT_DETAIL_DATA = "KEY_CHECKPOINT_DETAIL_DATA"
    }

    private val checkpointMediaAdapter by lazy {
        MediaAdapter(
                onMediaImageClick = {
                    // todo show image preview
                },
                onMediaDeleteClick = {
                    showDialogDeleteMedia(it)
                }
        )
    }

    private val checkpointDetailViewModel: CheckpointDetailViewModel by viewModel()
    private val checkpointListViewModel: CheckpointListViewModel by sharedViewModel()
    private val cameraViewModel: CameraViewModel by sharedViewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            (it.getSerializable(KEY_CHECKPOINT_DETAIL_DATA) as? CheckpointModel)?.let {model->
                checkpointDetailViewModel.setRawData(model)
                setupToolbar(model.name)
                setupData(model)
            }
        }

        // настройка слушателей кликов по элементу
        setupClickListeners()

        // настройка списка медиа
        setupMediaList()

        // настройка диалоговых окон
        setupDialogs()

        setupNavigation()

        progressView.setTextButton(strings[R.string.stop]){
            checkpointDetailViewModel.stopRfidScan()
        }

        checkpointDetailViewModel.rfidProgress.observe(viewLifecycleOwner, Observer {
            progressView.changeVisibility(it)
            progressView.changeTextVisibility(it)
        })

        checkpointDetailViewModel.descriptionObserver.observe(viewLifecycleOwner, Observer {
            etCheckpointDescription.setText(it)
        })

        checkpointDetailViewModel.rfidDataReceiver.observe(viewLifecycleOwner, EventObserver {
            tvRFID.text = resources.getString(R.string.rfid_s, it)
        })

        checkpointDetailViewModel.showError.observe(viewLifecycleOwner, EventObserver {
            showErrorToast()
        })
        checkpointDetailViewModel.isChanged.observe(viewLifecycleOwner, Observer {
            fabCheckpointSave.isVisible = it
        })
    }

    private fun showErrorToast() {
        Toast.makeText(
            activity, strings[R.string.error],
            Toast.LENGTH_LONG
        ).show()
    }

    private fun setupData(model: CheckpointModel) {
        etCheckpointDescription.setText("-")
    }

    private fun setupToolbar(title: String) {
        toolbarLayout.apply {
            tvTitle.text = title
            btnLeading.setOnClickListener {
                checkpointDetailViewModel.checkPopBack()
            }
        }
    }

    private fun setupNavigation() {
        checkpointDetailViewModel.navigateToCamera.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_checkpointDetailFragment_to_cameraFragment)
        })

        checkpointDetailViewModel.popNavigation.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })

        checkpointDetailViewModel.popAndRefresh.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
            checkpointListViewModel.getCheckpoints()
        })
    }

    private fun setupClickListeners() {

        // кнопка фото/видео нижнее меню
        llBottomPhotoVideo.setOnClickListener {
            //checkpointDetailViewModel.photoVideoClick()
        }

        // кнопка сканировать нижнее меню
        llBottomScan.setOnClickListener {
            checkpointDetailViewModel.startRfidScan()
        }

        // клик по кнопке сохранения дефекта
        fabCheckpointSave.setOnClickListener {
            checkpointDetailViewModel.checkAndSave()
        }


        etCheckpointDescription.doOnTextChanged { text, _, _, _ ->
            checkpointDetailViewModel.changeDescription(text)
        }
    }

    private fun setupDialogs() {
        checkpointDetailViewModel.showDialogChangedFields.observe(viewLifecycleOwner, EventObserver {
            showDialogChangedFields()
        })
        checkpointDetailViewModel.showDialogConfirmChangedFields.observe(viewLifecycleOwner, EventObserver {
            showDialogConfirmChange()
        })

        checkpointDetailViewModel.showSnackBar.observe(viewLifecycleOwner, EventObserver {
            mainViewModel.openSnackBar(strings[it])
        })
    }

    private fun setupMediaList() {

        rvAddCheckpointMedia.adapter = checkpointMediaAdapter

        checkpointDetailViewModel.mediaList.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                rvAddCheckpointMedia.visibility = View.VISIBLE
                tvMediaListNoData.visibility = View.GONE
                tvMediaList.text = formattedStrings[R.string.fragment_media_list_title].invoke(values = *arrayOf(it.size))
            } else {
                tvMediaListNoData.visibility = View.VISIBLE
                rvAddCheckpointMedia.visibility = View.GONE
                tvMediaList.text = formattedStrings[R.string.fragment_media_list_title].invoke(values = *arrayOf("0"))
            }
            checkpointMediaAdapter.items = it
        })

        cameraViewModel.capturedImage.observe(viewLifecycleOwner, EventObserver {
            checkpointDetailViewModel.addImage(it)
        })

    }

    private fun showDialogDeleteMedia(item: MediaUiModel) {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setMessage(strings[R.string.fragment_dialog_delete_subtitle])
                setPositiveButton(strings[R.string.fragment_add_dialog_btn_delete]
                ) { _, _ ->
                    checkpointDetailViewModel.deleteMedia(item)
                }
                setNegativeButton(strings[R.string.fragment_dialog_btn_cancel]) { _, _ -> }
            }
            builder.create()
        }
        alertDialog?.show()
    }

    private fun showDialogChangedFields() {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setMessage(strings[R.string.fragment_dialog_changed_fields])
                setPositiveButton(strings[R.string.fragment_dialog_btn_exit]
                ) { _, _ ->
                    findNavController().popBackStack()
                }
                setNegativeButton(strings[R.string.fragment_dialog_btn_cancel]) { _, _ -> }
            }
            builder.create()
        }
        alertDialog?.show()
    }

    private fun showDialogConfirmChange() {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle(strings[R.string.fragment_dialog_confirmed_title])
                setMessage(strings[R.string.fragment_dialog_confirmed_subtitle])
                setPositiveButton(strings[R.string.fragment_dialog_btn_save]) { _, _ ->
                    checkpointDetailViewModel.sendUpdate()
                }
                setNegativeButton(strings[R.string.fragment_dialog_btn_cancel]) { _, _ -> }
            }
            builder.create()
        }
        alertDialog?.show()
    }
}