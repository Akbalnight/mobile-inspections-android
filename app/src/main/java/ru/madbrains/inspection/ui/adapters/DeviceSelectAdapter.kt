package ru.madbrains.inspection.ui.adapters

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import ru.madbrains.inspection.base.BaseDiffCallback
import ru.madbrains.inspection.base.model.DiffItem
import ru.madbrains.inspection.ui.delegates.RouteUiModel
import ru.madbrains.inspection.ui.delegates.deviceSelectDelegate
import ru.madbrains.inspection.ui.delegates.routeDelegate

class DeviceSelectAdapter(

) : AsyncListDifferDelegationAdapter<DiffItem>(BaseDiffCallback()) {

    init {
        delegatesManager
            .addDelegate(deviceSelectDelegate())
    }
}