package ru.madbrains.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.madbrains.data.database.models.EquipmentItemDB

@Dao
interface EquipmentItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(detailedItem: EquipmentItemDB): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(detailedItemList: List<EquipmentItemDB>): Completable

    @Query("SELECT * FROM EquipmentItemDB")
    fun getItems(): Single<List<EquipmentItemDB>>

    @Query("DELETE FROM EquipmentItemDB")
    fun clean(): Completable
}
