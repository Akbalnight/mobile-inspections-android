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

fun mapGetRouteResp(resp: GetRouteResp): RouteModel {
    return with(resp) {
        RouteModel(
                id = id,
                name = name,
                code = code,
                duration = duration,
                routesData = routesData.map { mapGetRoutesDataResp(it) }
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
                equipments = equipments?.map { mapGetEquipmentResp(it) },
                techMap = techMap?.let { mapTechMapResp(it) }
        )
    }
}

fun mapGetEquipmentResp(resp: GetEquipmentResp): EquipmentModel {
    return with(resp) {
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
            warrantyFiles = warrantyFiles?.map { mapGetEquipmentFileResp(it) },
            attachmentFiles = attachmentFiles?.map { mapGetEquipmentFileResp(it) },
            deleted = deleted
        )
    }
}

fun mapGetEquipmentFileResp(resp: GetEquipmentFileResp): EquipmentFileModel {
    return with(resp) {
        EquipmentFileModel(
            id = id,
            url = url,
            name = name,
            extension = extension,
            date = date
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
                valueInputData = valueInputData,
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

fun mapGetFileResp(resp: GetFileResp): FileModel {
    return with(resp) {
        FileModel(
                id = id,
                url = url,
                name = name,
                extension = extension
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
