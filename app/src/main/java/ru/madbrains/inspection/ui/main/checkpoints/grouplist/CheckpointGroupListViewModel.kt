package ru.madbrains.inspection.ui.main.checkpoints.grouplist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.CheckpointGroupModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.ui.delegates.CheckpointUiModel

class CheckpointGroupListViewModel(private val routesInteractor: RoutesInteractor) : BaseViewModel() {
    private var _checkpointRawData: List<CheckpointGroupModel>? = null
    private val _checkPointList = MutableLiveData<List<CheckpointUiModel>>()
    val checkPointList: LiveData<List<CheckpointUiModel>> = _checkPointList

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _navigateToNextRoute = MutableLiveData<Event<CheckpointGroupModel>>()
    val navigateToNextRoute: LiveData<Event<CheckpointGroupModel>> = _navigateToNextRoute

    fun getCheckpoints() {
        routesInteractor.getCheckpoints()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _progressVisibility.postValue(true) }
            .doAfterTerminate { _progressVisibility.postValue(false) }
            .subscribe({ items ->
                _checkpointRawData = items
                _checkPointList.value = items.map { CheckpointUiModel(
                    id = it.parentId,
                    name = it.parentName?:"null"
                ) }
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    fun checkpointSelectClick(model: CheckpointUiModel) {
        _checkpointRawData?.find {
            it.parentId == model.id
        }?.run {
            _navigateToNextRoute.value = Event(this)
        }
    }
}