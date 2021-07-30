package ru.madbrains.domain.interactor

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import ru.madbrains.domain.model.*
import ru.madbrains.domain.repository.OfflineRepository
import ru.madbrains.domain.repository.RemoteRepository
import java.util.*

class RemoteInteractor(
    private val remoteRepository: RemoteRepository,
    private val offlineRepository: OfflineRepository
) {
    val syncedItemsFinish: Observable<String>
        get() = remoteRepository.syncedItemsFinish.subscribeOn(Schedulers.io())

    fun getDetours(): Single<List<DetourModel>> {
        val models = mutableListOf<DetourModel>()
        return remoteRepository.getDetoursStatuses().flatMap { statuses ->
            offlineRepository.insertDetourStatuses(statuses)
            remoteRepository.getDetours(
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
                    remoteRepository.freezeDetours(unfrozenIds)
                } else {
                    Completable.complete()
                }
            }.toSingle {
                models.filter { it.statusId != null }
            }
        }.subscribeOn(Schedulers.io())
    }

    fun getEquipments(): Single<List<EquipmentModel>> {
        return remoteRepository.getEquipments(emptyList(), emptyList())
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
        return remoteRepository.getDefects(
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
            offlineRepository.getChangedCheckpoints(),
            Function3 { b1: List<DetourModel>, b2: List<DefectModel>, b3: List<CheckpointModel> ->
                WrapChangedData(
                    b1,
                    b2,
                    b3
                )
            })
            .flatMapCompletable { wrap ->
                val tasks = arrayListOf<Completable>()
                wrap.detours.map { item ->
                    remoteRepository.updateDetour(item).andThen(
                        offlineRepository.insertDetour(item.copy(changed = false))
                            .doFinally {
                                remoteRepository.signalFinishSyncingItem(item.id)
                            }
                    )
                }.run {
                    tasks.addAll(this)
                }
                wrap.defects.map { item ->
                    val single = if (item.changed) updateDefect(item) else saveDefect(item)
                    single.flatMapCompletable { Completable.complete() }
                        .andThen(
                            offlineRepository.insertDefect(
                                item.copy(
                                    changed = false,
                                    created = false,
                                    files = item.files?.map { it.copy(isNew = false) }
                                )
                            ).doFinally {
                                remoteRepository.signalFinishSyncingItem(item.id)
                            }
                        )
                }.run {
                    tasks.addAll(this)
                }
                wrap.checkpoints.map { item ->
                    if (item.rfidCode != null) {
                        val single = updateCheckpoint(item.id, item.rfidCode)
                        Completable.fromSingle(single)
                            .andThen(
                                offlineRepository.insertCheckpoint(item.copy(changed = false))
                                    .doFinally { remoteRepository.signalFinishSyncingItem(item.id) }
                            )
                    } else {
                        Completable.complete()
                    }
                }.run {
                    tasks.addAll(this)
                }
                Completable.merge(tasks)
            }
            .doFinally {
                offlineRepository.finishSendSync(Date())
            }
            .subscribeOn(Schedulers.io())
    }

    fun getSyncEtcData(): Single<WrapEtcSync> {
        return Single.zip(
            remoteRepository.getEquipments(emptyList(), emptyList()),
            remoteRepository.getDefectTypical(),
            remoteRepository.getCheckpoints(),
            Function3 { b1: List<EquipmentModel>,
                        b2: List<DefectTypicalModel>,
                        b3: List<CheckpointModel>
                ->
                WrapEtcSync(b1, b2, b3)
            })
            .subscribeOn(Schedulers.io())

    }

    fun downloadDefectsMediaFilesArchive(defects: List<DefectModel>?): Single<WrapFile> {
        val ids = defects?.getAllFilesIds()
        val single: Single<WrapFile> = if (ids?.isNotEmpty() == true) {
            remoteRepository.downloadFileArchive(ids).flatMap { body ->
                offlineRepository.saveFileFromBody(
                    body,
                    OfflineRepository.ARCHIVE_DEFECTS_MEDIA,
                    RootDirType.Temp
                ).map {
                    WrapFile(it)
                }
            }
        } else {
            Single.just(WrapFile(null))
        }
        return single.subscribeOn(Schedulers.io())
    }

    fun downloadDocFilesArchive(routes: List<DetourModel>?): Single<WrapFile> {
        val ids = routes?.getAllFilesIds()
        val single: Single<WrapFile> = if (ids?.isNotEmpty() == true) {
            remoteRepository.downloadFileArchive(ids).flatMap { it ->
                offlineRepository.saveFileFromBody(
                    it,
                    OfflineRepository.ARCHIVE_DOCS,
                    RootDirType.Temp
                ).map {
                    WrapFile(it)
                }
            }
        } else {
            Single.just(WrapFile(null))
        }
        return single.subscribeOn(Schedulers.io())
    }

    private fun saveDefect(model: DefectModel): Single<String> {
        val files = model.files?.filter { it.isNew }?.mapNotNull { media ->
            offlineRepository.getFileInFolder(
                media.fileName,
                AppDirType.Local
            )
        }
        return remoteRepository.saveDefect(model, files).subscribeOn(Schedulers.io())
    }

    private fun updateDefect(model: DefectModel): Single<String> {
        val files = model.files?.filter { it.isNew }?.mapNotNull { media ->
            offlineRepository.getFileInFolder(
                media.fileName,
                AppDirType.Local
            )
        }
        return remoteRepository.updateDefect(model, files).subscribeOn(Schedulers.io())
    }

    private fun updateCheckpoint(id: String, rfidCode: String): Single<Any> {
        return remoteRepository.updateCheckpoint(id, rfidCode)
            .subscribeOn(Schedulers.io())
    }
}