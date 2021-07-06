package ru.madbrains.domain.interactor

import ru.madbrains.domain.repository.RfidListener
import ru.madbrains.domain.repository.RfidProgressListener
import ru.madbrains.domain.repository.RfidRepository

class RfidInteractor(
    private val rfidRepository: RfidRepository
) {
    fun startScan(progressListener: RfidProgressListener, listener: RfidListener) {
        rfidRepository.startScan(progressListener, listener)
    }

    fun stopScan() {
        rfidRepository.stopScan()
    }
}
