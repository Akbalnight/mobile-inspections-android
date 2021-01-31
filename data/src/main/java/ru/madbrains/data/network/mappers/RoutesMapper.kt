package ru.madbrains.data.network.mappers

import ru.madbrains.data.network.response.GetRouteResp
import ru.madbrains.domain.model.RouteModel
import ru.madbrains.domain.model.RouteStatus

fun mapGetRoutesResp(resp: GetRouteResp): RouteModel {
    return with(resp) {
        RouteModel(
            id = id,
            code = code,
            routeId = routeId,
            staffId = staffId,
            repeaterId = repeaterId,
            status = RouteStatus.values().find {
                it.id == statusId
            },
            statusName = statusName,
            routeName = routeName,
            name = name,
            staffName = staffName,
            dateStartPlan = dateStartPlan,
            dateFinishPlan = dateFinishPlan,
            dateStartFact = dateStartFact,
            dateFinishFact = dateFinishFact,
            saveOrderControlPoints = saveOrderControlPoints,
            takeIntoAccountTimeLocation = takeIntoAccountTimeLocation,
            takeIntoAccountDateStart = takeIntoAccountDateStart,
            takeIntoAccountDateFinish = takeIntoAccountDateFinish,
            possibleDeviationLocationTime = possibleDeviationLocationTime,
            possibleDeviationDateStart = possibleDeviationDateStart,
            possibleDeviationDateFinish = possibleDeviationDateFinish,
            isDefectExist = isDefectExist
        )
    }
}