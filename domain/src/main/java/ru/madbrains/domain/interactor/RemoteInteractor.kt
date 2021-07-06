package ru.madbrains.domain.interactor

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import okhttp3.ResponseBody
import retrofit2.Response
import ru.madbrains.domain.model.*
import ru.madbrains.domain.repository.DetoursRepository
import ru.madbrains.domain.repository.OfflineRepository
import java.util.*

class RemoteInteractor(
    private val routesRepository: DetoursRepository,
    private val offlineRepository: OfflineRepository
) {

    val syncedItemsRem = BehaviorSubject.create<String>()

    fun getDetours(): Single<List<DetourModel>> {
        val models = mutableListOf<DetourModel>()
        return routesRepository.getDetoursStatuses().flatMap { statuses ->
            offlineRepository.saveDetourStatuses(statuses)
            routesRepository.getDetours(
                statuses = statuses.getStatusesByType(
                    arrayOf(
                        DetourStatusType.PAUSED,
                        DetourStatusType.NEW
                    )
                )
            ).flatMapCompletable { items ->
                models.addAll(items)
                val unfrozenIds = models.filter { it.frozen != true }.map { it.id }
                if (unfrozenIds.isNotEmpty()) {
                    routesRepository.freezeDetours(unfrozenIds)
                } else {
                    Completable.complete()
                }
            }.toSingle {
                models.filter { it.statusId != null }
                    .filter { it -> it.route.routesData?.all { it.techMap != null } == true }
            }
        }.subscribeOn(Schedulers.io())
    }

    fun getCheckpoints(): Single<List<CheckpointModel>> {
        return routesRepository.getCheckpoints()
            .subscribeOn(Schedulers.io())
    }

    fun updateCheckpoint(id: String, rfidCode: String): Single<Any> {
        return routesRepository.updateCheckpoint(id, rfidCode)
            .subscribeOn(Schedulers.io())
    }

    fun getDefectTypical(): Single<List<DefectTypicalModel>> {
        return routesRepository.getDefectTypical()
            .subscribeOn(Schedulers.io())
    }

    fun getEquipments(): Single<List<EquipmentModel>> {
        return routesRepository.getEquipments(emptyList(), emptyList())
            .subscribeOn(Schedulers.io())
    }

    fun getFileArchive(fileIds: List<String>): Single<Response<ResponseBody>> {
        return routesRepository.downloadFileArchive(fileIds)
            .subscribeOn(Schedulers.io())
    }

    fun getDefects(
        id: String? = null,
        codes: List<String>? = null,
        dateDetectStart: String? = null,
        dateDetectEnd: String? = null,
        detourIds: List<String>? = null,
        defectNames: List<String>? = null,
        equipmentNames: List<String>? = null,
        equipmentIds: List<String>? = null,
        statusProcessId: String? = null
    ): Single<List<DefectModel>> {
        return routesRepository.getDefects(
            id,
            codes,
            dateDetectStart,
            dateDetectEnd,
            detourIds,
            defectNames,
            equipmentNames,
            equipmentIds,
            statusProcessId
        )
            .subscribeOn(Schedulers.io())
    }

    fun sendSyncDataAndRefreshDb(): Completable {
        return Single.zip(offlineRepository.getChangedDetours(),
            offlineRepository.getChangedDefects(),
            BiFunction { b1: List<DetourModel>, b2: List<DefectModel> -> Pair(b1, b2) })
            .flatMapCompletable { pair ->
                val tasks = arrayListOf<Completable>()
                pair.first.let { list ->
                    if (list.isNotEmpty()) {
                        val detourTasks = list.map { item ->
                            routesRepository.updateDetour(item).andThen(
                                offlineRepository.insertDetour(item.apply { changed = false })
                                    .doFinally {
                                        syncedItemsRem.onNext(item.id)
                                    }
                            )
                        }
                        tasks.addAll(detourTasks)
                    }
                }
                pair.second.let { list ->
                    if (list.isNotEmpty()) {
                        val defectsTasks = list.map { item ->
                            val single = if (item.changed) updateDefect(item) else saveDefect(item)
                            single.flatMapCompletable { Completable.complete() }
                                .andThen(
                                    offlineRepository.insertDefect(
                                        item.apply {
                                            changed = false
                                            created = false
                                            files = files?.map {
                                                it.copy(isNew = false)
                                            }
                                        }
                                    ).doFinally {
                                        syncedItemsRem.onNext(item.id)
                                    }
                                )
                        }
                        tasks.addAll(defectsTasks)
                    }
                }
                Completable.merge(tasks)
            }
            .doFinally {
                offlineRepository.finishSendSync(Date())
            }
            .subscribeOn(Schedulers.io())
    }

    private fun saveDefect(model: DefectModel): Single<String> {
        val files = model.files?.filter { it.isNew }?.mapNotNull { media ->
            offlineRepository.getFileInFolder(
                media.fileName,
                AppDirType.Local
            )
        }
        return routesRepository.saveDefect(model, files).subscribeOn(Schedulers.io())
    }

    private fun updateDefect(model: DefectModel): Single<String> {
        val files = model.files?.filter { it.isNew }?.mapNotNull { media ->
            offlineRepository.getFileInFolder(
                media.fileName,
                AppDirType.Local
            )
        }
        return routesRepository.updateDefect(model, files).subscribeOn(Schedulers.io())
    }
}
