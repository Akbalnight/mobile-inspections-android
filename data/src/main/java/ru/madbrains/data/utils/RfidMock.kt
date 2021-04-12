package ru.madbrains.data.utils

import android.os.Handler

class RfidMock: RfidDevice {

    private var dataListener: RfidListener? = null
    private val handler = Handler()

    override fun startScan(listener: RfidListener) {
        dataListener = listener

        handler.postDelayed({
            dataListener?.invoke("test_id_1")
        }, 1000);
    }



    override fun stopScan() {
        dataListener = null
    }
}