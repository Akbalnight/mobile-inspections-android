package ru.madbrains.inspection.ui.main.checkpoints.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.madbrains.domain.model.CheckpointGroupModel
import ru.madbrains.domain.model.CheckpointModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.ui.delegates.CheckpointUiModel

class CheckpointListViewModel : BaseViewModel() {
    private var _checkpointRawData: CheckpointGroupModel? = null

    private val _checkPointList = MutableLiveData<List<CheckpointUiModel>>()
    val checkPointList: LiveData<List<CheckpointUiModel>> = _checkPointList

    private val _navigateToNextRoute = MutableLiveData<Event<CheckpointModel>>()
    val navigateToNextRoute: LiveData<Event<CheckpointModel>> = _navigateToNextRoute

    fun setRouteData(it: CheckpointGroupModel) {
        _checkpointRawData = it

        _checkPointList.value = it.points.map { CheckpointUiModel(
            id = it.id,
            name = it.name
        ) }
    }

    fun checkpointSelectClick(model: CheckpointUiModel) {
        _checkpointRawData?.points?.find {
            it.id == model.id
        }?.run {
            _navigateToNextRoute.value = Event(this)
        }
    }
}