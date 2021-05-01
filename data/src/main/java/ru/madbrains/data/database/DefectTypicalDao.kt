package ru.madbrains.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.madbrains.data.database.models.DefectTypicalDB

@Dao
interface DefectTypicalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(detailedItem: DefectTypicalDB): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(detailedItemList: List<DefectTypicalDB>): Completable

    @Query("SELECT * FROM DefectTypicalDB")
    fun getItems(): Single<List<DefectTypicalDB>>

    @Query("DELETE FROM DefectTypicalDB")
    fun clean(): Completable
}
