package ru.madbrains.data.utils

typealias RfidListener = (id: String) -> Unit
interface  RfidDevice{
    fun startScan(listener: RfidListener)
    fun stopScan()
}