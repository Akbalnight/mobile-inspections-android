package ru.madbrains.inspection.ui.main.checkpoints.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.CheckpointGroupModel
import ru.madbrains.domain.model.CheckpointModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.ui.delegates.CheckpointUiModel

class CheckpointListViewModel(private val routesInteractor: RoutesInteractor) : BaseViewModel() {
    private var _checkpointRawData: List<CheckpointGroupModel>? = null
    private val _checkPointGroupList = MutableLiveData<List<CheckpointUiModel>>()
    val checkPointGroupList: LiveData<List<CheckpointUiModel>> = _checkPointGroupList

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private var _selectedGroupId: String? = null
    private val _selectedGroupUi = MutableLiveData<CheckpointGroupModel>()
    val selectedGroupUi: LiveData<CheckpointGroupModel> = _selectedGroupUi

    private val _navigateToNextRoute = MutableLiveData<Event<Unit>>()
    val navigateToNextRoute: LiveData<Event<Unit>> = _navigateToNextRoute

    private val _navigateToDetails = MutableLiveData<Event<CheckpointModel>>()
    val navigateToDetails: LiveData<Event<CheckpointModel>> = _navigateToDetails

    private fun setSelectedItem(id: String?) {
        _checkpointRawData?.find{id == it.parentId}?.let { group->
            _selectedGroupId = group.parentId
            _selectedGroupUi.value = group
        }
    }

    fun getCheckpoints() {
        routesInteractor.getCheckpoints()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _progressVisibility.postValue(true) }
            .doAfterTerminate { _progressVisibility.postValue(false) }
            .subscribe({ items ->
                _checkpointRawData = items
                _checkPointGroupList.value = items.map { CheckpointUiModel(
                    id = it.parentId,
                    name = it.parentName?:"null"
                ) }
                _selectedGroupId?.let{
                    setSelectedItem(it)
                }
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    fun checkpointSelectGroupClick(model: CheckpointUiModel) {
        _navigateToNextRoute.value = Event(Unit)
        setSelectedItem(model.id)
    }

    fun checkpointSelectClick(model: CheckpointUiModel) {
        _selectedGroupUi.value?.points?.find {
            it.id == model.id
        }?.run {
            _navigateToDetails.value = Event(this)
        }
    }
}