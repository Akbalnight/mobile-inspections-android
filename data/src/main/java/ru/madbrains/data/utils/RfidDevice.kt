package ru.madbrains.data.utils

typealias RfidListener = (id: String) -> Unit
typealias RfidProgressListener = (active: Boolean) -> Unit

interface RfidDevice {
    fun startScan(progressListener: RfidProgressListener, listener: RfidListener)
    fun stopScan()
}