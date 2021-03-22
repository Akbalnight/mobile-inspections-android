package ru.madbrains.inspection.ui.main.equipment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import okhttp3.ResponseBody
import ru.madbrains.data.extensions.toDDMMYYYY
import ru.madbrains.data.extensions.toHHmmYYYYMMDD
import ru.madbrains.data.network.ApiData
import ru.madbrains.domain.interactor.RoutesInteractor
import ru.madbrains.domain.model.EquipmentModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.EquipmentDetailMediaUiModel
import ru.madbrains.inspection.ui.delegates.FilesUiModel
import timber.log.Timber
import java.io.*
import java.util.*


class EquipmentViewModel(private val routesInteractor: RoutesInteractor) : BaseViewModel() {
    var savedEquipmentData: EquipmentModel? = null

    private val _equipmentImageList = MutableLiveData<List<DiffItem>>()
    val equipmentImageList: LiveData<List<DiffItem>> = _equipmentImageList
    private val _measuringPointsList = MutableLiveData<List<String>>()
    val measuringPointsList: LiveData<List<String>> = _measuringPointsList
    private val _commonSpecsList = MutableLiveData<Map<Int, Any?>>()
    val commonSpecsList: LiveData<Map<Int, Any?>> = _commonSpecsList
    private val _warrantyData = MutableLiveData<Map<Int, String?>>()
    val warrantyData: LiveData<Map<Int, String?>> = _warrantyData

    private val _files = MutableLiveData<List<FilesUiModel>>()
    val files: LiveData<List<FilesUiModel>> = _files

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    fun setEquipmentData(data: EquipmentModel) {
        savedEquipmentData = data
    }

    fun prepareCommonData() {
        savedEquipmentData?.let { data ->
            data.getImageUrls().map {
                EquipmentDetailMediaUiModel(url = ApiData.apiUrl + it.url)
            }.let { images ->
                _equipmentImageList.value =
                    if (images.isNotEmpty()) images else arrayListOf(EquipmentDetailMediaUiModel("placeholder"))
            }

            _commonSpecsList.value = mapOf(
                R.string.equipment_specs_type_equipment to data.typeEquipment,
                R.string.equipment_specs_id_sap to data.sapId,
                R.string.equipment_specs_name to data.name,
                R.string.equipment_specs_code_tech_place to data.techPlacePath,
                R.string.equipment_specs_name_tech_place to data.techPlace,
                R.string.equipment_specs_construction_type to data.constructionType,
                R.string.equipment_specs_material to data.material,
                R.string.equipment_specs_size to data.size,
                R.string.equipment_specs_weight to data.weight,
                R.string.equipment_specs_manufacturer to data.manufacturer,
                R.string.equipment_specs_del_mark to if (data.deleted == true) R.string.yes else R.string.no,
                R.string.equipment_specs_valid_by to data.dateFinish?.toDDMMYYYY()
            )

            if (data.measuringPoints != null) {
                _measuringPointsList.value = data.measuringPoints
            }

            if (data.dateWarrantyStart != null) {
                _warrantyData.value = mapOf(
                    R.string.equipment_specs_warranty_start to data.dateWarrantyStart?.toDDMMYYYY(),
                    R.string.equipment_specs_warranty_end to data.dateWarrantyFinish?.toDDMMYYYY()
                )
            }
        }
    }

    fun prepareFiles(){
        savedEquipmentData?.let { data ->
            _files.value = data.getAllDocs().map {
                FilesUiModel(
                    id = it.id,
                    url = it.url,
                    name = it.name,
                    extension = it.extension,
                    date = Date().toHHmmYYYYMMDD()
                )
            }
        }
    }

    fun openFile(file: FilesUiModel, directory: File) {
        routesInteractor.downloadFile(ApiData.apiUrl + file.url)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { _progressVisibility.postValue(true) }
            .doAfterTerminate { _progressVisibility.postValue(false) }
            .subscribe({ response ->
                if (response.isSuccessful) {
                    response.body()?.run {
                        saveFile(this, file.name, directory)
                    }
                }
            }, {
                it.printStackTrace()
            })
            .addTo(disposables)
    }

    private fun saveFile(body: ResponseBody, name: String, directory: File) {
        try {
            val file = File(directory, name)
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(file)
                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                }
                outputStream.flush()
                Timber.d("debug_dmm outputStream ${outputStream}")
                Timber.d("debug_dmm directory ${directory.absolutePath}")
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}