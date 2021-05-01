package ru.madbrains.data.utils

import android.os.Handler
import android.os.Looper
import androidx.core.os.HandlerCompat
import com.pow.api.cls.RfidPower
import com.uhf.api.cls.Reader
import timber.log.Timber
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RfidReader : RfidDevice {
    private val handler = Handler()
    private var scanIsOn = false
    private val rPower = RfidPower(RfidPower.PDATYPE.ZoomSmart)
    private val reader = Reader()
    private var dataListener: RfidListener? = null
    private var mProgressListener: RfidProgressListener? = null

    init {
        try {
            FileOutputStream("/sys/class/bj_dev/func/uhf_en")
            Timber.d("device found")
        } catch (e: Throwable) {
            throw Throwable("device is not found")
        }
    }

    private val scanRunnable = Runnable {
        executorService.execute {
            scan()
        }
    }
    private val executorService: ExecutorService = Executors.newFixedThreadPool(4)
    private val mainThreadHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())

    companion object {
        private const val deviceSource = "/dev/ttyHSL3"
        private const val rType = 1
        private val ants: IntArray = intArrayOf(1)
        private val tagCnt: IntArray = intArrayOf(0)
        private const val timeout: Short = 500
    }

    private fun scan() {
        val er = reader.TagInventory_Raw(ants, ants.size, timeout, tagCnt)
        Timber.d("debug_dmm RFID loop $er")
        if (er == Reader.READER_ERR.MT_OK_ERR) {
            val tagInfo = reader.TAGINFO()
            if (reader.GetNextTag(tagInfo) == Reader.READER_ERR.MT_OK_ERR) {
                onDataReceived(Reader.bytes_Hexstr(tagInfo.EpcId))
            } else if (scanIsOn) {
                handler.postDelayed(this.scanRunnable, 0)
            }
        }
    }

    override fun startScan(progressListener: RfidProgressListener, listener: RfidListener) {
        dataListener = listener
        mProgressListener = progressListener

        handler.postDelayed({
            executorService.execute {
                if (!scanIsOn) {
                    Timber.d("debug_dmm RFID ${rPower.GetDevPath()}")
                    onProgressChanged(true)
                    scanIsOn = true
                    rPower.PowerDown()
                    rPower.PowerUp()
                    reader.InitReader_Notype(deviceSource, rType)
                    scan()
                }
            }
        }, 0)
    }


    override fun stopScan() {
        onProgressChanged(active = false, clean = true)
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
            stopScan()
        }
    }

    private fun onProgressChanged(active: Boolean, clean: Boolean = false) {
        mainThreadHandler.post {
            mProgressListener?.invoke(active)
            if (clean) {
                mProgressListener = null
            }
        }
    }
}