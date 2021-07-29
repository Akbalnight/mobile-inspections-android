package ru.madbrains.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.madbrains.data.database.models.DefectItemDB
import ru.madbrains.data.database.models.DefectTypicalDB
import ru.madbrains.data.database.models.DetourItemDB
import ru.madbrains.data.database.models.EquipmentItemDB

@Database(
    entities = [
        DetourItemDB::class,
        DefectItemDB::class,
        EquipmentItemDB::class,
        DefectTypicalDB::class
    ], version = 11, exportSchema = false
)
abstract class HcbDatabase : RoomDatabase() {
    abstract fun detourItemDao(): DetourItemDao
    abstract fun defectItemDao(): DefectItemDao
    abstract fun equipmentItemDao(): EquipmentItemDao
    abstract fun defectTypicalDao(): DefectTypicalDao
}
