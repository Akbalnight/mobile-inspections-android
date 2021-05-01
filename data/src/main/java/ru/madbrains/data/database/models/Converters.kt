package ru.madbrains.data.database.models

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.madbrains.domain.model.ExtraDataModel
import ru.madbrains.domain.model.FileModel
import ru.madbrains.domain.model.RouteModel
import java.util.*

class Converters {
    val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.run { Date(this) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromRouteModel(json: String?): RouteModel? {
        return if (json == null) null else gson.fromJson<RouteModel>(json, RouteModel::class.java)
    }

    @TypeConverter
    fun toRouteModel(value: RouteModel?): String? {
        return if (value == null) null else gson.toJson(value)
    }

    @TypeConverter
    fun fromExtraDataModelList(json: String?): List<ExtraDataModel>? {
        return if (json == null) null else gson.fromJson<List<ExtraDataModel>>(
            json,
            object : TypeToken<ArrayList<ExtraDataModel>>() {}.type
        )
    }

    @TypeConverter
    fun toExtraDataModelList(value: List<ExtraDataModel>?): String? {
        return if (value == null) null else gson.toJson(value)
    }

    @TypeConverter
    fun fromFileModelList(json: String?): List<FileModel>? {
        return if (json == null) null else gson.fromJson<List<FileModel>>(
            json,
            object : TypeToken<ArrayList<FileModel>>() {}.type
        )
    }

    @TypeConverter
    fun toFileModelList(value: List<FileModel>?): String? {
        return if (value == null) null else gson.toJson(value)
    }

    @TypeConverter
    fun fromStringList(json: String?): List<String>? {
        return if (json == null) null else gson.fromJson<List<String>>(
            json,
            object : TypeToken<ArrayList<String>>() {}.type
        )
    }

    @TypeConverter
    fun toStringList(value: List<String>?): String? {
        return if (value == null) null else gson.toJson(value)
    }
}
