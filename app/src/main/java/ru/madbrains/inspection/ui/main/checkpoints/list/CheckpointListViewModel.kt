package ru.madbrains.inspection.ui.main.checkpoints.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.domain.interactor.DetoursInteractor
import ru.madbrains.domain.model.CheckpointModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.ui.delegates.CheckpointUiModel

class CheckpointListViewModel(private val detoursInteractor: DetoursInteractor) : BaseViewModel() {
    private var _checkpointRawData: List<CheckpointModel>? = null
    private val _checkPointList = MutableLiveData<List<CheckpointUiModel>>()
    val checkPointList: LiveData<List<CheckpointUiModel>> = _checkPointList

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _navigateToDetails = MutableLiveData<Event<CheckpointModel>>()
    val navigateToDetails: LiveData<Event<CheckpointModel>> = _navigateToDetails

    fun getCheckpoints() {
        detoursInteractor.getCheckpointsRemote()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _progressVisibility.postValue(true) }
            .doAfterTerminate { _progressVisibility.postValue(false) }
            .subscribe({ items ->
                _checkpointRawData = items
                _checkPointList.value = items.map {
                    CheckpointUiModel(
                        id = it.id,
                        name = it.name,
                        hasRfid = it.rfidCode != null
                    )
                }
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    fun checkpointSelectClick(model: CheckpointUiModel) {
        _checkpointRawData?.find {
            it.id == model.id
        }?.run {
            _navigateToDetails.value = Event(this)
        }
    }
}