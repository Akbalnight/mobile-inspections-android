package ru.madbrains.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.madbrains.data.database.models.DetourItemDB
import ru.madbrains.data.database.models.DetourWithDefectCountItemDB

@Dao
interface DetourItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(detailedItem: DetourItemDB): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(detailedItemList: List<DetourItemDB>): Completable

    @Query("SELECT * FROM DetourItemDB ORDER BY dateStartPlan DESC")
    fun getItems(): Single<List<DetourItemDB>>

    @Query("SELECT r.*, COUNT(d.id) as defectCount FROM DetourItemDB as r LEFT JOIN DefectItemDB as d ON d.detourId = r.id WHERE d.statusProcessId!=:eliminatedId AND d.created = 1 GROUP BY r.id ORDER BY r.dateStartPlan DESC")
    fun getItemsWithDefectCount(eliminatedId: String): Single<List<DetourWithDefectCountItemDB>>

    @Query("SELECT * FROM DetourItemDB WHERE changed = 1 ORDER BY dateStartPlan DESC")
    fun getChangedItems(): Single<List<DetourItemDB>>

    @Query("DELETE FROM DetourItemDB")
    fun clean(): Completable
}
