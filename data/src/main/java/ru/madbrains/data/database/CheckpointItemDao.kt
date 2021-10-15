package ru.madbrains.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.madbrains.data.database.models.CheckpointItemDB

@Dao
interface CheckpointItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(detailedItem: CheckpointItemDB): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(detailedItemList: List<CheckpointItemDB>): Completable

    @Query("SELECT * FROM CheckpointItemDB")
    fun getItems(): Single<List<CheckpointItemDB>>

    @Query("DELETE FROM CheckpointItemDB")
    fun clean(): Completable

    @Query("SELECT * FROM CheckpointItemDB WHERE changed = 1")
    fun getChangedItems(): Single<List<CheckpointItemDB>>

    @Query("SELECT * FROM CheckpointItemDB WHERE rfidCode = :rfidCode")
    fun getItemWithRfidCode(rfidCode: String): Single<List<CheckpointItemDB>>
}
