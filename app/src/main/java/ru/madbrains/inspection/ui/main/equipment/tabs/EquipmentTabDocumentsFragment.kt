package ru.madbrains.inspection.ui.main.equipment.tabs

import android.content.Intent
import android.os.Bundle
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_equipment_tab_documents.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.ui.adapters.FilesAdapter
import ru.madbrains.inspection.ui.main.equipment.EquipmentViewModel

class EquipmentTabDocumentsFragment : BaseFragment(R.layout.fragment_equipment_tab_documents) {

    private val equipmentViewModel: EquipmentViewModel by sharedViewModel()
    private val filesAdapter by lazy {
        FilesAdapter(
            onFileClick = { file ->
                equipmentViewModel.openFile(file)
            }
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        equipmentViewModel.prepareFiles()
        observeData()
    }

    private fun observeData() {
        equipmentViewModel.files.observe(viewLifecycleOwner, Observer { files ->
            rvFiles.run {
                adapter = filesAdapter.apply {
                    items = files
                }
            }
        })
        equipmentViewModel.startFileIntent.observe(viewLifecycleOwner, EventObserver {
            try {
                val uri = FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName.toString() + ".file_provider",
                    it.first
                )
                val newIntent = Intent()
                newIntent.action = Intent.ACTION_VIEW
                newIntent.setDataAndType(uri, it.second)
                newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(newIntent)
            } catch (e: Throwable) {
                e.printStackTrace()
                equipmentViewModel.showError()
            }
        })
    }
}