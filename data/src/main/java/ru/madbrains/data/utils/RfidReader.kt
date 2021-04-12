package ru.madbrains.data.utils

import android.os.Handler
import android.os.Looper
import androidx.core.os.HandlerCompat
import com.pow.api.cls.RfidPower
import com.uhf.api.cls.Reader
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RfidReader: RfidDevice {

    private val handler = Handler()
    private var scanIsOn = false
    private val rPower = RfidPower(RfidPower.PDATYPE.ZoomSmart)
    private val reader = Reader()
    private var dataListener: RfidListener? = null

    private val scanRunnable = Runnable {
        executorService.execute {
            scan()
        }
    }
    private val executorService: ExecutorService = Executors.newFixedThreadPool(4)
    private val mainThreadHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())

    companion object{
        private const val deviceSource = "/dev/ttyHSL3"
        private const val rType = 1
        private val ants: IntArray = intArrayOf(1)
        private val tagCnt: IntArray = intArrayOf(0)
        private const val timeout: Short = 50
    }

    private fun scan(){
        val er = reader.TagInventory_Raw(ants, ants.size, timeout, tagCnt)
        Timber.d("debug_dmm RFID loop $er")
        if (er == Reader.READER_ERR.MT_OK_ERR) {
            val tagInfo = reader.TAGINFO()
            if (reader.GetNextTag(tagInfo) == Reader.READER_ERR.MT_OK_ERR) {
                onDataReceived(Reader.bytes_Hexstr(tagInfo.EpcId))
                stopScan()
            } else if (scanIsOn) {
                handler.postDelayed(this.scanRunnable, 1000)
            }
        }
    }

    override fun startScan(listener: RfidListener) {
        handler.postDelayed({
            executorService.execute {
                dataListener = listener
                if (!scanIsOn) {
                    scanIsOn = true
                    Timber.d("debug_dmm RFID ${rPower.GetDevPath()}")
                    rPower.PowerDown()
                    rPower.PowerUp()
                    reader.InitReader_Notype(deviceSource, rType)
                    scan()
                }
            }
        }, 0)
    }



    override fun stopScan() {
        dataListener = null
        scanIsOn = false
        executorService.execute {
            reader.AsyncStopReading()
            reader.CloseReader()
            rPower.PowerDown()
        }
        handler.removeCallbacks(scanRunnable)
    }

    private fun onDataReceived(id: String) {
        Timber.d("debug_dmm RFID ...info= $id")
        mainThreadHandler.post {
            dataListener?.invoke(id)
        }
    }
}