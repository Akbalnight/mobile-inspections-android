package ru.madbrains.inspection.ui.main.equipment.tabs

import android.os.Bundle
import android.os.Environment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_equipment_tab_documents.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.ui.adapters.FilesAdapter
import ru.madbrains.inspection.ui.main.equipment.EquipmentViewModel

class EquipmentTabDocumentsFragment : BaseFragment(R.layout.fragment_equipment_tab_documents) {

    private val equipmentViewModel: EquipmentViewModel by sharedViewModel()
    private val filesAdapter by lazy {
        FilesAdapter(
            onFileClick = { file->
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.let { directory ->
                    equipmentViewModel.openFile(file, directory)
                }
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
    }
}