package ru.madbrains.data.network.mappers

import ru.madbrains.data.network.models.*
import ru.madbrains.data.network.response.*
import ru.madbrains.domain.model.*

fun mapGetDetoursResp(remote: DetoursRemote): DetourModel {
    return with(remote) {
        DetourModel(
            id = id,
            code = code,
            routeId = routeId,
            staffId = staffId,
            repeaterId = repeaterId,
            statusId = statusId,
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
            frozen = frozen,
            route = mapGetRouteResp(route)
        )
    }
}

private fun mapGetRouteResp(remote: RouteRemote): RouteModel {
    return with(remote) {
        RouteModel(
            id = id,
            name = name,
            code = code,
            duration = duration,
            routesData = routesData.map { mapGetRoutesDataResp(it) },
            routeMaps = routeMaps?.map { mapGetFileResp(it) }
        )
    }
}

private fun mapGetRoutesDataResp(remote: RouteDataRemote): RouteDataModel {
    return with(remote) {
        RouteDataModel(
            id = id,
            techMapId = techMapId,
            controlPointId = controlPointId,
            rfidCode = rfidCode,
            routeMapId = routeMapId,
            routeId = routeId,
            duration = duration,
            xLocation = xLocation,
            yLocation = yLocation,
            position = position,
            equipments = equipment?.map { mapGetEquipmentResp(it) },
            techMap = techMap?.let { mapTechMapResp(it) },
            completed = completed
        )
    }
}

private fun mapTechMapResp(remote: TechMapRemote): TechMapModel {
    return with(remote) {
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

private fun mapGetTechOperationResp(remote: TechOperationRemote): TechOperationModel {
    return with(remote) {
        TechOperationModel(
            id = id,
            name = name,
            code = code,
            needInputData = needInputData,
            labelInputData = labelInputData,
            valueInputData = valueInputData,
            equipmentStop = equipmentStop,
            increasedDanger = increasedDanger,
            duration = duration,
            techMapId = techMapId,
            position = position
        )
    }
}

fun mapGetEquipmentResp(remote: EquipmentRemote): EquipmentModel {
    return with(remote) {
        EquipmentModel(
            id = id,
            code = code,
            name = name,
            parentId = parentId,
            isGroup = isGroup,
            techPlace = techPlace,
            techPlacePath = techPlacePath,
            sapId = sapId,
            constructionType = constructionType,
            material = material,
            size = size,
            weight = weight,
            manufacturer = manufacturer,
            dateFinish = dateFinish,
            measuringPoints = measuringPoints,
            dateWarrantyStart = dateWarrantyStart,
            dateWarrantyFinish = dateWarrantyFinish,
            typeEquipment = typeEquipment,
            warrantyFiles = warrantyFiles?.map { mapGetFileResp(it) },
            attachmentFiles = attachmentFiles?.map { mapGetFileResp(it) },
            deleted = deleted
        )
    }
}

fun mapGetCheckpointResp(resp: GetCheckpointResp): CheckpointModel {
    return with(resp) {
        CheckpointModel(
            id = id,
            code = code,
            name = name,
            rfidCode = rfidCode
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

fun mapGetDefectStatusResp(resp: GetDetourStatusResp): DetourStatus {
    return with(resp) {
        DetourStatus(
            id = id,
            name = name,
            code = code
        )
    }
}

fun mapGetDefectsResp(resp: GetDefectsResp): DefectModel {
    return with(resp) {
        DefectModel(
            id = id,
            equipmentId = equipmentId,
            staffDetectId = staffDetectId,
            defectTypicalId = defectTypicalId,
            description = description,
            dateDetectDefect = dateDetectDefect,
            detourId = detourId,
            files = files?.map { mapGetFileResp(it) },
            defectName = defectName,
            equipmentName = equipmentName,
            statusProcessId = statusProcessId,
            extraData = extraData?.map { mapGetExtraDataResp(it) }

        )
    }
}

fun mapGetFileResp(remote: FileRemote): FileModel {
    return with(remote) {
        FileModel(
            id = id,
            fileId = fileId,
            url = url,
            name = name,
            extension = extension,
            date = date
        )
    }
}

fun mapGetExtraDataResp(resp: GetExtraDataResp): ExtraDataModel {
    return with(resp) {
        ExtraDataModel(
            dateDetectDefect = dateDetectDefect,
            staffDetectId = staffDetectId,
            description = description,
            detoursId = detoursId
        )
    }
}
