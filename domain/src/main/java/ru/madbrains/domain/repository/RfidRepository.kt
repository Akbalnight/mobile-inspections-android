package ru.madbrains.domain.repository

typealias RfidListener = (id: String) -> Unit
typealias RfidProgressListener = (active: Boolean) -> Unit

interface RfidRepository {
    fun startScan(progressListener: RfidProgressListener, listener: RfidListener)
    fun stopScan()
}