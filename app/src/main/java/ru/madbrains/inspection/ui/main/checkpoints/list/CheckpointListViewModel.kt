package ru.madbrains.inspection.ui.main.checkpoints.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import ru.madbrains.domain.interactor.OfflineInteractor
import ru.madbrains.domain.model.CheckpointModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.ui.delegates.CheckpointUiModel

class CheckpointListViewModel(private val offlineInteractor: OfflineInteractor) : BaseViewModel() {
    private var _checkpointRawData: List<CheckpointModel>? = null
    private var _checkpointUIData: List<CheckpointUiModel>? = null
    private val _checkPointList = MutableLiveData<List<CheckpointUiModel>>()
    val checkPointList: LiveData<List<CheckpointUiModel>> = _checkPointList

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _navigateToDetails = MutableLiveData<Event<CheckpointModel>>()
    val navigateToDetails: LiveData<Event<CheckpointModel>> = _navigateToDetails

    fun getCheckpoints() {
        offlineInteractor.getCheckpoints()
            .observeOn(Schedulers.io())
            .doOnSubscribe { _progressVisibility.postValue(true) }
            .doAfterTerminate { _progressVisibility.postValue(false) }
            .subscribe({ items ->
                _checkpointRawData = items
                _checkpointUIData = items.map {
                    CheckpointUiModel(
                        id = it.id,
                        name = it.name,
                        hasRfid = it.rfidCode != null
                    )
                }
                _checkPointList.postValue(_checkpointUIData)
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    fun checkpointSelectClick(model: CheckpointUiModel) {
        _checkpointRawData?.find {
            it.id == model.id
        }?.run {
            _navigateToDetails.postValue(Event(this))
        }
    }

    fun searchText(text: String) {
        val items = _checkpointUIData?.filter { it.name.contains(text, ignoreCase = true) }
        _checkPointList.postValue(items)
    }
}