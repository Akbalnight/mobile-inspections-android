package ru.madbrains.data.network.mappers

import ru.madbrains.data.network.response.GetPlanTechOperationsResp
import ru.madbrains.domain.model.PlanTechOperationsModel

fun mapGetPlanTechOperationsResp(resp: GetPlanTechOperationsResp): PlanTechOperationsModel {
    return with(resp) {
        PlanTechOperationsModel(
            id = id,
            dataId = dataId,
            name = name,
            needInputData = needInputData,
            labelInputData = labelInputData,
            techMapName = techMapName
        )
    }
}