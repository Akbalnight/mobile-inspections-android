package ru.madbrains.inspection.ui.main.equipmentList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.model.EquipmentModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.EquipmentListUiModel

class EquipmentListViewModel() : BaseViewModel() {
    private val _equipmentList = MutableLiveData<List<DiffItem>>()
    val equipmentList: LiveData<List<DiffItem>> = _equipmentList

    fun setEquipmentList(list: List<EquipmentModel>) {
        val equipments = mutableListOf<DiffItem>().apply {
            list.map { equipment ->
                add(
                    EquipmentListUiModel(
                        id = equipment.id,
                        name = equipment.name
                    )
                )
            }
        }
        _equipmentList.value = equipments
    }
}