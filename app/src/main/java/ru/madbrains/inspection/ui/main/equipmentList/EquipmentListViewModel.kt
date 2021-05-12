package ru.madbrains.inspection.ui.main.equipmentList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.interactor.DetoursInteractor
import ru.madbrains.domain.model.AppDirType
import ru.madbrains.domain.model.EquipmentModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.EquipmentListUiModel

class EquipmentListViewModel(
    private val detoursInteractor: DetoursInteractor
) : BaseViewModel() {
    private val _equipmentList = MutableLiveData<List<DiffItem>>()
    val equipmentList: LiveData<List<DiffItem>> = _equipmentList
    private val _navigateToEquipment = MutableLiveData<Event<EquipmentModel>>()
    val navigateToEquipment: LiveData<Event<EquipmentModel>> = _navigateToEquipment
    var itemList: List<EquipmentModel>? = null

    fun setEquipmentList(list: List<EquipmentModel>) {
        val equipments = mutableListOf<DiffItem>().apply {
            list.map { equipment ->
                add(
                    EquipmentListUiModel(
                        id = equipment.id,
                        name = equipment.name,
                        type = equipment.typeEquipment,
                        images = equipment.getImageUrls().map {
                            detoursInteractor.getFileInFolder(it.fileName, AppDirType.Docs)
                        }
                    )
                )
            }
        }
        _equipmentList.value = equipments
        itemList = list
    }

    fun toEquipmentFragment(item: EquipmentListUiModel) {
        itemList?.find { item.id == it.id }?.let {
            _navigateToEquipment.value = Event(it)
        }
    }
}