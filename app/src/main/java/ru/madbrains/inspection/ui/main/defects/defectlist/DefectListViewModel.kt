package ru.madbrains.inspection.ui.main.defects.defectlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.DefectModel
import ru.madbrains.inspection.base.BaseViewModel

class DefectListViewModel(private val routesInteractor: RoutesInteractor) : BaseViewModel() {

    private val _defectList = MutableLiveData<List<DefectModel>>()
    val defectList: LiveData<List<DefectModel>> = _defectList

    fun getDefectList() {
        routesInteractor.getDefects()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ items ->
                    //   defectTypicalModels.addAll(items)
                    // updateDefectTypicalList()
                    _defectList.value = items
                }, {
                    it.printStackTrace()
                })
                .addTo(disposables)
    }
}