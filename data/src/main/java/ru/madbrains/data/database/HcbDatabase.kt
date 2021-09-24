package ru.madbrains.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.madbrains.data.database.models.*

@Database(
    entities = [
        DetourItemDB::class,
        DefectItemDB::class,
        EquipmentItemDB::class,
        DefectTypicalDB::class,
        CheckpointItemDB::class
    ], version = 14, exportSchema = false
)
abstract class HcbDatabase : RoomDatabase() {
    abstract fun detourItemDao(): DetourItemDao
    abstract fun defectItemDao(): DefectItemDao
    abstract fun equipmentItemDao(): EquipmentItemDao
    abstract fun defectTypicalDao(): DefectTypicalDao
    abstract fun checkpointItemDao(): CheckpointItemDao
}
