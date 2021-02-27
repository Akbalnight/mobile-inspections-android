package ru.madbrains.data.network.mappers

import ru.madbrains.data.network.response.*
import ru.madbrains.domain.model.*

fun mapGetDetoursResp(resp: GetDetoursResp): DetourModel {
    return with(resp) {
        DetourModel(
            id = id,
            code = code,
            routeId = routeId,
            staffId = staffId,
            repeaterId = repeaterId,
            status = DetourStatus.values().find {
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
            isDefectExist = isDefectExist,
            route = mapGetRouteResp(route)
        )
    }
}

fun mapGetRouteResp(resp: GetRouteResp): RouteModel {
    return with(resp) {
        RouteModel(
            id = id,
            name = name,
            code = code,
            duration = duration,
            routeData = routesData.map { mapGetRoutesDataResp(it) }
        )
    }
}

fun mapGetRoutesDataResp(resp: GetRouteDataResp): RouteDataModel {
    return with(resp) {
        RouteDataModel(
            id = id,
            techMapId = techMapId,
            controlPointId = controlPointId,
            routeMapId = routeMapId,
            routeId = routeId,
            duration = duration,
            xLocation = xLocation,
            yLocation = yLocation,
            position = position,
            equipments = equipments.map { mapGetEquipmentResp(it) },
            techMap = mapTechMapResp(techMap)
        )
    }
}

fun mapGetEquipmentResp(resp: GetEquipmentResp): EquipmentModel {
    return with(resp) {
        EquipmentModel(
            id = id,
            code = code,
            name = name,
            markId = markId,
            modelId = modelId,
            parentId = parentId,
            isGroup = isGroup
        )
    }
}

fun mapTechMapResp(resp: GetTechMapResp): TechMapModel {
    return with(resp) {
        TechMapModel(
            id = id,
            name = name,
            code = code,
            dateStart = dateStart,
            techMapsStatusId = techMapsStatusId,
            parentId = parentId,
            isGroup = isGroup,
            techOperations = techOperations.map { mapGetTechOperationResp(it) }
        )
    }
}

fun mapGetTechOperationResp(resp: GetTechOperationResp): TechOperationModel {
    return with(resp) {
        TechOperationModel(
            id = id,
            name = name,
            code = code,
            needInputData = needInputData,
            labelInputData = labelInputData,
            equipmentStop = equipmentStop,
            increasedDanger = increasedDanger,
            duration = duration,
            techMapId = techMapId,
            position = position
        )
    }
}




fun mapGetRoutePointsResp(resp: GetRoutePointResp): RoutePointModel {
    return with(resp) {
        RoutePointModel(
            id = id,
            rowNumber = rowNumber,
            controlPointId = controlPointId,
            controlPointName = controlPointName,
            techMapName = techMapName,
            detoursId = detoursId,
            position = position,
            duration = duration
        )
    }
}

fun mapGetPlanTechOperationsResp(resp: GetPlanTechOperationsResp): PlanTechOperationsModel {
    return with(resp) {
        PlanTechOperationsModel(
            id = id,
            dataId = dataId,
            name = name,
            needInputData = needInputData,
            labelInputData = labelInputData,
            techMapName = techMapName,
            position = position
        )
    }
}


fun mapGetDefectTypicalResp(resp: GetDefectTypicalResp): DefectTypicalModel {
    return with(resp) {
        DefectTypicalModel(
            id = id,
            name = name,
            code = code
        )
    }
}

fun mapGetEquipmentsResp(resp: GetEquipmentsResp): EquipmentsModel {
    return with(resp) {
        EquipmentsModel(
            id = id,
            name = name,
            isGroup = isGroup,
            controlPointId = controlPointId,
            markName = markName,
            modelName = modelName,
            defects = defects,
            equipmentFiles = equipmentFiles
        )
    }
}