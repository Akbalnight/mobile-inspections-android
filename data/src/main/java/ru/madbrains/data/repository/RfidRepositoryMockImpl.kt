package ru.madbrains.data.repository

import android.os.Handler
import ru.madbrains.domain.repository.RfidListener
import ru.madbrains.domain.repository.RfidProgressListener
import ru.madbrains.domain.repository.RfidRepository

class RfidRepositoryMockImpl : RfidRepository {

    private var dataListener: RfidListener? = null
    private var mProgressListener: RfidProgressListener? = null
    private val handler = Handler()

    override fun startScan(progressListener: RfidProgressListener, listener: RfidListener) {
        dataListener = listener
        mProgressListener = progressListener

        mProgressListener?.invoke(true)
        handler.postDelayed({
            dataListener?.invoke("test_id_1")
            stopScan()
        }, 5000)
    }

    override fun stopScan() {
        mProgressListener?.invoke(false)
        dataListener = null
        mProgressListener = null
    }
}