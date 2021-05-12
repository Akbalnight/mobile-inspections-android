package ru.madbrains.data.network.mappers

import ru.madbrains.data.network.models.*
import ru.madbrains.domain.model.*

fun mapDetoursReq(resp: DetourModel): DetoursRemote {
    return with(resp) {
        DetoursRemote(
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
            route = mapGetRouteReq(route)
        )
    }
}

private fun mapGetRouteReq(resp: RouteModel): RouteRemote {
    return with(resp) {
        RouteRemote(
            id = id,
            name = name,
            code = code,
            duration = duration,
            routesData = routesData.map { mapGetRoutesDataReq(it) },
            routeMaps = routeMaps?.map { mapGetFileReq(it) }
        )
    }
}

private fun mapGetRoutesDataReq(resp: RouteDataModel): RouteDataRemote {
    return with(resp) {
        RouteDataRemote(
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
            equipment = equipments?.map { mapGetEquipmentReq(it) },
            techMap = techMap?.let { mapTechMapReq(it) },
            completed = completed
        )
    }
}

private fun mapGetEquipmentReq(resp: EquipmentModel): EquipmentRemote {
    return with(resp) {
        EquipmentRemote(
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
            warrantyFiles = warrantyFiles?.map { mapGetFileReq(it) },
            attachmentFiles = attachmentFiles?.map { mapGetFileReq(it) },
            deleted = deleted
        )
    }
}

private fun mapTechMapReq(resp: TechMapModel): TechMapRemote {
    return with(resp) {
        TechMapRemote(
            id = id,
            name = name,
            code = code,
            dateStart = dateStart,
            techMapsStatusId = techMapsStatusId,
            parentId = parentId,
            isGroup = isGroup,
            techOperations = techOperations.map { mapGetTechOperationReq(it) }
        )
    }
}

private fun mapGetTechOperationReq(resp: TechOperationModel): TechOperationRemote {
    return with(resp) {
        TechOperationRemote(
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

private fun mapGetFileReq(resp: FileModel): FileRemote {
    return with(resp) {
        FileRemote(
            id = id,
            fileId = fileId,
            url = url,
            name = name,
            extension = extension,
            date = date,
            routeMapName = routeMapName
        )
    }
}