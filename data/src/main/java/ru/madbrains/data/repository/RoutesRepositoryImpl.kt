package ru.madbrains.data.repository

import com.squareup.moshi.Json
import io.reactivex.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.http.Multipart
import ru.madbrains.data.network.api.InspectionApi
import ru.madbrains.data.network.mappers.*
import ru.madbrains.data.network.request.*
import ru.madbrains.domain.model.*
import ru.madbrains.domain.repository.DetoutsRepository
import java.io.File

class RoutesRepositoryImpl(
    private val inspectionApi: InspectionApi
) : DetoutsRepository {
    override fun getDetours(): Single<List<DetourModel>> {
        val request = GetDetoursReq()
        return inspectionApi.getDetours(request).map { resp ->
            resp.map { mapGetDetoursResp(it) }
        }
    }

    override fun getRoutePoints(routeId: String): Single<List<RoutePointModel>> {
        val request = GetRoutePointsReq(
                routeId = routeId
        )
        return inspectionApi.getRoutePoints(request).map { resp ->
            resp.map { mapGetRoutePointsResp(it) }
        }
    }

    override fun getPlanTechOperations(dataId: String): Single<List<PlanTechOperationsModel>> {
        val request = GetPlanTechOperationsReq(
                dataId = dataId
        )
        return inspectionApi.getPlanTechOperations(request).map { resp ->
            resp.map { mapGetPlanTechOperationsResp(it) }
        }
    }

    override fun getDefectTypical(): Single<List<DefectTypicalModel>> {
        val request = GetDefectTypicalReq()
        return inspectionApi.getDefectTypical(request).map { resp ->
            resp.map { mapGetDefectTypicalResp(it) }
        }
    }

    override fun getEquipments(
            names: List<String>,
            uuid: List<String>
    ): Single<List<EquipmentsModel>> {
        val request = GetEquipmentsReq(names = names, controlPointIds = uuid)
        return inspectionApi.getEquipments(request).map { resp ->
            resp.map { mapGetEquipmentsResp(it) }
        }
    }

    override fun getDefects(id: String?,
                            codes: List<String>?,
                            dateDetectStart: String?,
                            dateDetectEnd: String?,
                            detourIds: List<String>?,
                            defectNames: List<String>?,
                            equipmentNames: List<String>?,
                            statusProcessId: String?): Single<List<DefectModel>> {
        val request = GetDefectsReq(
                id = id,
                codes = codes,
                dateDetectStart = dateDetectStart,
                dateDetectEnd = dateDetectEnd,
                detourIds = detourIds,
                defectNames = defectNames,
                equipmentNames = equipmentNames,
                statusProcessId = statusProcessId)
        return inspectionApi.getDefects(request).map { resp ->
            resp.map { mapGetDefectsResp(it) }
        }

    }

    override fun saveDefect(files: List<File>?, detoursId: String?, equipmentId: String?, staffDetectId: String?, defectTypicalId: String?, description: String?, dateDetectDefect: String?): Single<String> {
        val request = CreateDefectReq(
                detoursId = detoursId,
                equipmentId = equipmentId,
                staffDetectId = staffDetectId,
                defectTypicalId = defectTypicalId,
                description = description,
                dateDetectDefect = dateDetectDefect
        )

      //  files
        val fileList = files?.map {
            MultipartBody.Part.createFormData(
                    name = "files[]",
                    filename = it.name,
                    body = it.asRequestBody("multipart/form-data".toMediaTypeOrNull()))

        }

        var multiParts: List<MultipartBody.Part>? = null
        files?.let { list  ->
            if(list.isNotEmpty()) {
                val body = MultipartBody.Builder().apply {
                    list?.forEach {item ->
                        addFormDataPart(name = "files",
                                filename = item.name,
                                body = item.asRequestBody("multipart/form-data".toMediaTypeOrNull()))
                    }

                }.build()
                multiParts = body.parts
            }
        }

       /* var dfs:List<MultipartBody.Part>? = null

        body?.let { dfs = it.parts }


        var multiRequest: RequestBody? = null
        files?.let {list ->
            if(list.isNotEmpty()){
                val _body = MultipartBody.Builder().apply {
                    setType(MultipartBody.FORM)
                    list?.forEach {
                        addFormDataPart(name = "files",
                                filename = it.name,
                                body = it.asRequestBody("multipart/form-data".toMediaTypeOrNull()))
                    }
                }.build()
                multiRequest = _body
            }


        }
*/

/*
        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM).apply {
            addFormDataPart("type", "booking")
            addFormDataPart("user", "username")
            addFormDataPart("message", "message text goes here")
            addFormDataPart("contact_number", "0123456789")
            addFormDataPart("contact_email", "email@address.com")
            // my files are List<ByteArray>, okhttp has a few utility methods like .toRequestBody for various types like below
            files.forEachIndexed { index, bytes ->
                addFormDataPart("files[]", "$index.jpg", bytes.toRequestBody("multipart/form-data".toMediaTypeOrNull(), 0, bytes.size))
            }
        }.build()*/

        return inspectionApi.saveDefect(request, multiParts)
    }

}