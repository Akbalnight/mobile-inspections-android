package ru.madbrains.data.utils

import android.os.Handler
import com.pow.api.cls.RfidPower
import com.uhf.api.cls.Reader
import timber.log.Timber
import java.util.concurrent.Executors

class RfidReader: RfidDevice {

    private val handler = Handler()
    private var scanIsOn = false
    private val rPower = RfidPower(RfidPower.PDATYPE.ZoomSmart)
    private val reader = Reader()
    private var dataListener: RfidListener? = null

    private lateinit var runnable: Runnable

    companion object{
        private const val deviceSource = "/dev/ttyHSL3"
        private const val rType = 1
        private val ants: IntArray = intArrayOf(1)
        private val tagCnt: IntArray = intArrayOf(0)
        private const val timeout: Short = 500
    }

    init {
        runnable = Runnable {
            Timber.d("RFID ${rPower.GetDevPath()}")
            reader.InitReader_Notype(deviceSource, rType)

            var er = reader.TagInventory_Raw(ants, ants.size, timeout, tagCnt)

            if (er == Reader.READER_ERR.MT_OK_ERR) {
                val tagInfo = reader.TAGINFO()
                er = reader.GetNextTag(tagInfo)
                if (er == Reader.READER_ERR.MT_OK_ERR) {
                    onDataReceived(Reader.bytes_Hexstr(tagInfo.EpcId))
                }
            }
            if(scanIsOn) {
                handler.post(this.runnable)
            }
        }
    }

    override fun startScan(listener: RfidListener) {
        dataListener = listener
        if(!scanIsOn){
            scanIsOn = true
            Executors.newSingleThreadExecutor().execute {
                rPower.PowerDown()
                rPower.PowerUp()
                handler.post(this.runnable)
            }
        }
    }



    override fun stopScan() {
        dataListener = null
        scanIsOn = false
        Executors.newSingleThreadExecutor().execute {
            reader.AsyncStopReading()
            reader.CloseReader()
            rPower.PowerDown()
        }
        handler.removeCallbacks(runnable)
    }

    private fun onDataReceived(id: String) {
        Timber.d("RFID ...info= $id")
        dataListener?.invoke(id)
    }
}