package ru.madbrains.data.network.mappers

import ru.madbrains.data.database.models.*
import ru.madbrains.domain.model.*

fun fromDetourItemDB(resp: DetourItemDB): DetourModel {
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
            route = route,
            changed = changed
        )
    }
}

fun toDetourItemDB(resp: DetourModel): DetourItemDB {
    return with(resp) {
        DetourItemDB(
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
            route = route,
            changed = changed
        )
    }
}

fun fromDefectItemDB(resp: DefectItemDB): DefectModel {
    return with(resp) {
        DefectModel(
            id = id,
            equipmentId = equipmentId,
            staffDetectId = staffDetectId,
            defectTypicalId = defectTypicalId,
            description = description,
            dateDetectDefect = dateDetectDefect,
            detourId = detourId,
            files = files,
            defectName = defectName,
            equipmentName = equipmentName,
            statusProcessId = statusProcessId,
            extraData = extraData,
            created = created,
            changed = changed
        )
    }
}

fun toDefectItemDB(resp: DefectModel): DefectItemDB {
    return with(resp) {
        DefectItemDB(
            id = id,
            equipmentId = equipmentId,
            staffDetectId = staffDetectId,
            defectTypicalId = defectTypicalId,
            description = description,
            dateDetectDefect = dateDetectDefect,
            detourId = detourId,
            files = files,
            defectName = defectName,
            equipmentName = equipmentName,
            statusProcessId = statusProcessId,
            extraData = extraData,
            created = created,
            changed = changed
        )
    }
}

fun fromEquipmentItemDB(resp: EquipmentItemDB): EquipmentModel {
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
            warrantyFiles = warrantyFiles,
            attachmentFiles = attachmentFiles,
            deleted = deleted
        )
    }
}

fun toEquipmentItemDB(resp: EquipmentModel): EquipmentItemDB {
    return with(resp) {
        EquipmentItemDB(
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
            warrantyFiles = warrantyFiles,
            attachmentFiles = attachmentFiles,
            deleted = deleted
        )
    }
}

fun fromDefectTypicalDB(resp: DefectTypicalDB): DefectTypicalModel {
    return with(resp) {
        DefectTypicalModel(
            id = id,
            name = name,
            code = code
        )
    }
}

fun toDefectTypicalDB(resp: DefectTypicalModel): DefectTypicalDB {
    return with(resp) {
        DefectTypicalDB(
            id = id,
            name = name,
            code = code
        )
    }
}

fun toCheckpointItemDB(resp: CheckpointModel): CheckpointItemDB {
    return with(resp) {
        CheckpointItemDB(
            id = id,
            code = code,
            name = name,
            rfidCode = rfidCode
        )
    }
}