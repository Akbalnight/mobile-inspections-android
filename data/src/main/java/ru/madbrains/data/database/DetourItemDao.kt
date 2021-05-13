package ru.madbrains.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.madbrains.data.database.models.DetourItemDB

@Dao
interface DetourItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(detailedItem: DetourItemDB): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(detailedItemList: List<DetourItemDB>): Completable

    @Query("SELECT * FROM DetourItemDB B ORDER BY dateStartPlan DESC")
    fun getItems(): Single<List<DetourItemDB>>

    @Query("SELECT * FROM DetourItemDB WHERE changed = 1 ORDER BY dateStartPlan DESC")
    fun getChangedItems(): Single<List<DetourItemDB>>

    @Query("DELETE FROM DetourItemDB")
    fun clean(): Completable
}
