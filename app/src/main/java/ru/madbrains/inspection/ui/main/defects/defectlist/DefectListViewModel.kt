package ru.madbrains.inspection.ui.main.defects.defectlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.DefectModel
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.DefectListUiModel

class DefectListViewModel(private val routesInteractor: RoutesInteractor) : BaseViewModel() {


    private val _defectList = MutableLiveData<List<DiffItem>>()
    val defectList: LiveData<List<DiffItem>> = _defectList

    private val defectListModels = mutableListOf<DefectModel>()

    fun getDefectList() {
        routesInteractor.getDefects()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ items ->
                    defectListModels.addAll(items)
                    updateDefectList()
                }, {
                    it.printStackTrace()
                })
                .addTo(disposables)
    }

    private fun updateDefectList() {
        val defects = mutableListOf<DiffItem>().apply {
            defectListModels.map { defect ->
                add(
                        DefectListUiModel(
                                id = defect.id,
                                name = defect.defectName.orEmpty()
                        )
                )
            }
        }
        _defectList.value = defects
    }
}