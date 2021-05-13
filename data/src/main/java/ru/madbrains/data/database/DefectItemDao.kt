package ru.madbrains.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.madbrains.data.database.models.DefectItemDB

@Dao
interface DefectItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(detailedItem: DefectItemDB): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(detailedItemList: List<DefectItemDB>): Completable

    @Query("SELECT * FROM DefectItemDB WHERE equipmentId in (:equipmentIds)")
    fun getItemsByEquipment(equipmentIds: List<String>): Single<List<DefectItemDB>>

    @Query("SELECT * FROM DefectItemDB ORDER BY dateDetectDefect DESC")
    fun getItems(): Single<List<DefectItemDB>>

    @Query("SELECT * FROM DefectItemDB WHERE changed = 1 OR created = 1")
    fun getChangedItems(): Single<List<DefectItemDB>>

    @Query("DELETE FROM DefectItemDB WHERE changed = 0 AND created = 0")
    fun clean(): Completable

    @Query("DELETE FROM DefectItemDB WHERE id = :id")
    fun del(id:String): Completable
}