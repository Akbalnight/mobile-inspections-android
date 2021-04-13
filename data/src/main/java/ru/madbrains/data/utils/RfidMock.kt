package ru.madbrains.data.utils

import android.os.Handler
import java.util.*

class RfidMock: RfidDevice {

    private var dataListener: RfidListener? = null
    private var mProgressListener: RfidProgressListener? = null
    private val handler = Handler()

    override fun startScan(progressListener: RfidProgressListener, listener: RfidListener) {
        dataListener = listener
        mProgressListener = progressListener

        mProgressListener?.invoke(true)
        handler.postDelayed({
            dataListener?.invoke("test_id_${Random().nextInt(61)}")
            stopScan()
        }, 10000);
    }



    override fun stopScan() {
        mProgressListener?.invoke(false)
        dataListener = null
        mProgressListener = null
    }
}